package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.AvroKotlin.ResourceKtx.resourceUrl
import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.trimToNull
import io.toolisticon.avro.kotlin.declaration.ProtocolDeclaration
import io.toolisticon.avro.kotlin.declaration.SchemaDeclaration
import io.toolisticon.avro.kotlin.logical.AvroLogicalType
import io.toolisticon.avro.kotlin.model.*
import io.toolisticon.avro.kotlin.model.wrapper.AvroProtocol
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.*
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificData
import org.apache.avro.specific.SpecificRecord
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.writeText
import kotlin.reflect.KClass

/**
 * Central declaration of constants and utils.
 */
object AvroKotlin {

  /**
   * Encapsulates [Schema#Parser#parse] for the relevant input types.
   */
  @Suppress("ClassName")
  object parseSchema {
    operator fun invoke(json: JsonString, isRoot: Boolean = false): AvroSchema = AvroSchema(json, isRoot)
    operator fun invoke(file: File): AvroSchema = AvroSchema(file)
    operator fun invoke(path: Path): AvroSchema = AvroSchema(path)
    operator fun invoke(resource: URL): AvroSchema = AvroSchema(resource)
    operator fun invoke(resource: String, classLoader: ClassLoader = DEFAULT_CLASS_LOADER): AvroSchema = invoke(resourceUrl(resource, classLoader))

    operator fun invoke(recordClass: Class<*>): AvroSchema {
      require(recordClass.isAssignableFrom(SpecificRecord::class.java)) { "recordClass needs to be SpecificRecord: ${recordClass.simpleName}" }
      val schema: Schema = SpecificData(recordClass.classLoader).getSchema(recordClass)
      return AvroSchema(schema)
    }

    operator fun invoke(recordClass: KClass<*>): AvroSchema = invoke(recordClass.java)
  }

  @Suppress("ClassName")
  object parseProtocol {
    private fun parse(inputStream: InputStream) = AvroProtocol(
      protocol = Protocol.parse(inputStream)
    )

    operator fun invoke(json: JsonString): AvroProtocol = parse(json.inputStream())
    operator fun invoke(file: File): AvroProtocol = parse(file.inputStream())
    operator fun invoke(resource: URL): AvroProtocol = parse(resource.openStream())
    operator fun invoke(resource: String): AvroProtocol = invoke(resourceUrl(resource))
  }

  /**
   * Marker bytes according to Avro schema specification v1.
   */
  val AVRO_V1_HEADER = SingleObjectEncodedBytes.AVRO_V1_HEADER

  fun avroType(schema: AvroSchema): AvroType = AvroType.avroType(schema)

  fun name(name: String): Name = Name(name)
  fun namespace(namespace: String): Namespace = Namespace(namespace)
  fun canonicalName(namespace: String, name: String): CanonicalName = CanonicalName(namespace(namespace), name(name))


  fun createGenericRecord(schema: AvroSchema, receiver: GenericData.Record.() -> Unit) = GenericData.Record(schema.get()).apply {
    receiver.invoke(this)
  }


  object Separator {

    /**
     * Default separator used in canonical name.
     */
    const val NAME = "."

    /**
     * File separator as defined by [File.separator].
     */
    val FILE = File.separator

    /**
     * Replace path file-separator dash with namespace dots.
     */
    fun dashToDot(string: String): String = string.replace(FILE, NAME)

    /**
     * Replace namespace dots with file-separator dash.
     */
    fun dotToDash(string: String): String = string.replace(NAME, FILE)

  }

  /**
   * Set of reserved keywords.
   */
  object Reserved {

    private val FIELD_RESERVED = Collections
      .unmodifiableSet(HashSet(mutableListOf("name", "type", "doc", "default", "aliases")))

    val SCHEMA: Set<String> = setOf("doc", "fields", "items", "name", "namespace", "size", "symbols", "values", "type", "aliases")
  }

  /**
   * Default charset to use is [Charsets#UTF_8]
   */
  val UTF_8 = Charsets.UTF_8


  /**
   * Default [ClassLoader] for resource loading.
   */
  val DEFAULT_CLASS_LOADER: ClassLoader = AvroKotlin::class.java.classLoader

  /**
   * An avro declaration file should have a path that matches its canonicalName, the same way a java file has to
   * be defined in the correct package.
   * @throws AvroDeclarationMismatchException
   */
  // FIXME
//  @Throws(AvroDeclarationMismatchException::class)
//  fun verifyPackagePathConvention(actual: GenericAvroDeclarationFqn, expected: GenericAvroDeclarationFqn): Unit = try {
//    require(actual == expected)
//  } catch (e: Exception) {
//    throw AvroDeclarationMismatchException(actual, expected)
//  }


  /**
   * Gets the nullable [Documentation] of the given string.
   */
  fun documentation(value: String?): Documentation? = value?.trimToNull()?.let { Documentation(it) }

  /**
   * Gets the nullabla [Documentation] of given [Schema].
   *
   * @see #documentation(String)
   */
  fun documentation(schema: Schema): Documentation? = documentation(schema.doc)

  fun documentation(field: Schema.Field): Documentation? = documentation(field.doc())

  @Suppress("ClassName")
  object write {
    operator fun invoke(json: JsonString, path: Path) = path.writeText(json.value)
    operator fun invoke(json: JsonString, file: File) = file.writeText(json.value)

    operator fun invoke(schema: AvroSchema, directory: Directory) = directory.write(
      fqn = schema.canonicalName,
      type = AvroSpecification.SCHEMA,
      content = schema.json
    )

    operator fun invoke(schema: SchemaDeclaration, directory: Directory) = directory.write(
      fqn = schema.canonicalName,
      type = AvroSpecification.SCHEMA,
      content = schema.source.json
    )

    operator fun invoke(protocol: AvroProtocol, directory: Directory) = directory.write(
      fqn = protocol.canonicalName,
      type = AvroSpecification.PROTOCOL,
      content = protocol.json
    )

    operator fun invoke(protocol: ProtocolDeclaration, directory: Directory) = directory.write(
      fqn = protocol.canonicalName,
      type = AvroSpecification.PROTOCOL,
      content = protocol.source.json
    )
  }


  fun logicalTypeName(logicalType: LogicalType?) = logicalType?.let { LogicalTypeName(it.name) }

  val avroLogicalTypes by lazy {
    LogicalTypes.getCustomRegisteredTypes().values.filterIsInstance<AvroLogicalType<*>>()
      .toList()
  }

  val genericDataWithConversions: GenericData by lazy {
    GenericData().apply {
      avroLogicalTypes.forEach { logicalType ->
        addLogicalTypeConversion(logicalType.conversion)
      }
    }
  }

  fun genericRecordToJson(record: GenericData.Record, genericData: GenericData = genericDataWithConversions): JsonString {

    val jsonBytes = ByteArrayOutputStream().use {
      val dw = GenericDatumWriter<Any>(record.schema, genericData)
      val encoder = EncoderFactory.get().jsonEncoder(record.schema, it, true)
      dw.write(record, encoder)
      encoder.flush()
      it.toString(UTF_8)
    }

    return JsonString(jsonBytes)
  }

  fun genericRecordFromJson(json: JsonString, schema: AvroSchema, genericData: GenericData = genericDataWithConversions): GenericData.Record {
    val reader = GenericDatumReader<Any>(schema.get(), schema.get(), genericData)

    return reader.read(null, DecoderFactory.get().jsonDecoder(schema.get(), json.inputStream())) as GenericData.Record
  }


  fun <T : Any> Result<List<T>?>.orEmpty(): List<T> = getOrNull() ?: emptyList()


  object StringKtx {
    fun String?.trimToNull() = if (this.isNullOrBlank()) null else this

    fun <K : Any, V : Any> Map<K, V>.toReadableString() = StringBuilder()
      .apply {
        this@toReadableString.forEach { (k, v) ->
          appendLine("$k:")
          appendLine("\t$v")
          appendLine()
        }
      }
      .toString()

    fun ifTrue(condition: Boolean, caseTrue: String, caseFalse: String = ""): String = if (condition)
      caseTrue
    else
      caseFalse

    fun String.shorten(maxLength: Int = 100, append: String = "...") = if (length > maxLength)
      this.take(maxLength) + append
    else this

    fun Any?.nullableToString(
      prefix: String = "",
      suffix: String = "",
      maxLength: Int = Int.MAX_VALUE,
      append: String = ""
    ) = if (this != null)
      "$prefix${this.toString().shorten(maxLength, append)}$suffix"
    else
      ""

    fun String.firstUppercase() = this.replaceFirstChar(Char::uppercase)

    fun String.trailingSlash() = if (this.startsWith("/")) this else "/$this"

    fun csv(vararg values: String?) = listOfNotNull(*values).joinToString(separator = ", ")

  }

  object SchemaKtx {

    val Schema.path: Path get() = (Namespace(namespace) + Name(name)).toPath(AvroSpecification.SCHEMA)

    /**
     * A [File] with [Protocol#path] based on given directory. Use to read or write protocol from file system.
     */
    fun Schema.file(dir: File): Path = Directory(dir.toPath()).resolve(this.path)

    /**
     * Write schema json content to directory, using [Schema#path].
     */
    fun Schema.writeToDirectory(dir: File): Path {
      val target: File = file(dir).toFile()
      target.writeText(toString(true))

      return target.toPath()
    }

    /**
     * Creates a schema compliant generic record.
     *
     * @param receiver - a lambda that can modify the record (basically: use `put` to fill in data
     * @return a schema compliant record instance
     */
    fun Schema.createGenericRecord(receiver: GenericData.Record.() -> Unit) = GenericData.Record(this).apply { receiver.invoke(this) }


    fun Schema.writeToDirectory(dir: Directory, path: Path = this.path): File {
      val content = toString(true)
      val file = dir.resolve(path).toFile()

      return file.apply { writeText(toString(true)) }
    }
  }

  object ResourceKtx {

    fun loadJsonString(resource: String, classLoader: ClassLoader = DEFAULT_CLASS_LOADER): JsonString = JsonString(
      json = resourceUrl(resource, classLoader).readText(UTF_8).trim()
    )

    /**
     * Gets a resource URL from static `resources` folder.
     *
     * @param resource the resource to get
     * @param classLoader optional classloader, defaults to DEFAULT_CLASSLOADER
     * @return url of given resource
     * @throws IllegalArgumentException when resource does not exist
     */
    @Throws(IllegalArgumentException::class)
    fun resourceUrl(resource: String, classLoader: ClassLoader = DEFAULT_CLASS_LOADER): URL = with(resource.removePrefix("/")) {
      requireNotNull(classLoader.getResource(this)) { "Resource not found: $this" }
    }

    /**
     * URL of resource using path based on namespace and name.
     *
     * @param namespace the namespace (aka directory path to look in)
     * @param name of the file to load
     * @param fileExtension file extension
     * @param prefix optional prefix-path if resource is not under root in classpath
     * @param classLoader optional classloader if not given, the classloader of AvroKotlinLib is used
     * @return URL pointing to resource.
     */
//    TODO  fun resource(
//        namespace: Namespace,
//        name: Name,
//        fileExtension: String,
//        prefix: String? = null,
//        classLoader: ClassLoader = AvroKotlin.DEFAULT_CLASS_LOADER
//      ): URL {
//        val fqn = GenericAvroDeclarationFqn(namespace, name, fileExtension)
//        val path = fqn.path
//
//        val resource: String = (if (prefix != null) {
//          "$prefix${File.separator}$path"
//        } else {
//          path.toString()
//        }).removePrefix(File.separator)
//
//        return requireNotNull(classLoader.getResource(resource)) { "resource not found: $path" }
//      }


    /**
     * Gets [resource] URL and uses the given parser function to create either a [Schema] or [Protocol] instance.
     *
     * @param namespace namespace aka directory path
     * @param name the file name
     * @param fileExtension the file extension
     * @param prefix optional path if not in resources root
     * @param classLoader optional ClassLoader, default is [AvroKotlinLib#DEFAULT_CLASS_LOADER]
     * @param parser parser function, either Schema.Parser() or Protocol.parse()
     */
//   TODO   fun <T : JsonProperties> parseFromResource(
//        namespace: Namespace,
//        name: Name,
//        fileExtension: String,
//        prefix: String? = null,
//        classLoader: ClassLoader = AvroKotlin.DEFAULT_CLASS_LOADER,
//        parser: (InputStream) -> T
//      ): T {
//        return resourceUrl(namespace, name, fileExtension, prefix, classLoader).openStream().use { parser.invoke(it) }
//      }

    @Throws(IllegalStateException::class)
    fun rootResource(classLoader: ClassLoader = DEFAULT_CLASS_LOADER): URL = checkNotNull(
      classLoader.getResource("")
    ) { "no root resource found for classLoader:$classLoader" }

    fun findAvroResources(prefix: String? = null, classLoader: ClassLoader = DEFAULT_CLASS_LOADER): Map<AvroSpecification, List<File>> {
      val rootPath = Path(rootResource(classLoader).path).resolve(prefix ?: "")

      return rootPath.toFile().walk()
        .filter { it.isFile }
        .filter { AvroSpecification.EXTENSIONS.contains(it.extension) }
        .groupBy(keySelector = { AvroSpecification.valueOfExtension(it.extension) })
    }
  }

  object ProtocolKtx {

    val Protocol.documentation: Documentation? get() = documentation(this.doc)
    val Protocol.Message.documentation: Documentation? get() = documentation(this.doc)


    // TODO: hopefully we will not need this reflection stuff, but if the above createRecord method loses the reference, we might.
    private val schemaFieldPosition = Schema.Field::class.java.getDeclaredField("position").apply { isAccessible = true }
    fun Schema.Field.resetPosition() = schemaFieldPosition.set(this, -1)

    /**
     * Protocol file path based on namespace, name and extension.
     */
    val Protocol.path: Path get() = (Namespace(namespace) + Name(name)).toPath(AvroSpecification.PROTOCOL)

    /**
     * A [File] with [Protocol#path] based on given directory. Use to read or write protocol from file system.
     */
    fun Protocol.file(dir: File): Path = Directory(dir.toPath()).resolve(this.path)


    /**
     * Write protocol json content to directory, using [Protocol#path].
     */
    fun Protocol.writeToDirectory(dir: File): Path {
      val target: File = file(dir).toFile()
      target.writeText(toString(true))

      return target.toPath()
    }

    /**
     * Get message from protocol by name.
     */
    fun Protocol.message(name: String): Protocol.Message = requireNotNull(this.messages[name]) { "No protocol message with name '$name' found." }

  }
}
