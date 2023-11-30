package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.firstUppercase
import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.trailingSlash
import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.trimToNull
import io.toolisticon.avro.kotlin.model.*
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.*
import org.apache.avro.Schema.Type
import org.apache.avro.generic.GenericData
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

/**
 * Central declaration of constants and utils.
 */
object AvroKotlin {

  /**
   * Encapsulates [Schema#Parser#parse] for the relevant input types.
   */
  object parseSchema {
    operator fun invoke(json: JsonString, isRoot: Boolean = false): AvroSchema = AvroSchema(json, isRoot)
    operator fun invoke(file: File): AvroSchema = AvroSchema(file)
    operator fun invoke(path: Path): AvroSchema = AvroSchema(path)
    operator fun invoke(resource: URL): AvroSchema = AvroSchema(resource)
  }

  object parseProtocol {
    private fun parse(inputStream: InputStream) = AvroProtocol(
      protocol = Protocol.parse(inputStream)
    )

    operator fun invoke(json: JsonString): AvroProtocol = parse(json.inputStream())
    operator fun invoke(file: File): AvroProtocol = parse(file.inputStream())
    operator fun invoke(resource: URL): AvroProtocol = parse(resource.openStream())
  }

  /**
   * Marker bytes according to Avro schema specification v1.
   */
  val AVRO_V1_HEADER = SingleObjectEncodedBytes.AVRO_V1_HEADER

  object FileExtension {
    /**
     * Avro Schema files end with `.avsc`.
     */
    const val SCHEMA: String = "avsc"

    /**
     * Avro protocol files end with `.avpr`.
     */
    const val PROTOCOL: String = "avpr"
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
    val PROTOCOL_KEYWORDS: Set<String> = setOf("namespace", "protocol", "doc", "messages", "types", "errors")
    val PROTOCOL_MESSAGES = setOf("doc", "response", "request", "errors", "one-way")

    private val FIELD_RESERVED = Collections
      .unmodifiableSet(HashSet(mutableListOf("name", "type", "doc", "default", "aliases")))

    val SCHEMA: Set<String> = setOf("doc", "fields", "items", "name", "namespace", "size", "symbols", "values", "type", "aliases")
  }

  /**
   * Default charset to use is [Charsets#UTF_8]
   */
  val UTF_8 = Charsets.UTF_8


  /**
   * The primitive types.
   */
  val PRIMITIVE_TYPES = EnumSet.of(
    Type.BOOLEAN, Type.BYTES, Type.DOUBLE, Type.FLOAT,
    Type.INT, Type.LONG, Type.NULL, Type.STRING
  )

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


  fun logicalTypeName(logicalType: LogicalType?) = logicalType?.let { LogicalTypeName(it.name) }

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

    fun loadResource(name: String): String = with(name.trailingSlash()) {
      return resourceUrl(this).readText()
    }

    fun resourceUrl(resource: String): URL = requireNotNull(
      {}::class.java.getResource(resource.trailingSlash())
    ) { "resource not found: $resource" }


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
//      fun resource(
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
//      fun <T : JsonProperties> parseFromResource(
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
    fun rootResource(classLoader: ClassLoader = DEFAULT_CLASS_LOADER): URL = classLoader.getResource("")
      ?: throw IllegalStateException("no root resource found for classLoader:$classLoader")

    fun findAvroResources(prefix: String? = null, classLoader: ClassLoader = DEFAULT_CLASS_LOADER) {
      val rootPath = Path(rootResource(classLoader).path).resolve(prefix ?: "")

      Files.walk(rootPath)
        .map { it.toFile() }
        .forEach { println(it) }
    }

  }

  object ProtocolKtx {

    val Protocol.fingerprint: AvroFingerprint get() = AvroFingerprint(this)
    val Protocol.hashCode: AvroHashCode get() = AvroHashCode(this)

    val Protocol.documentation: Documentation? get() = documentation(this.doc)
    val Protocol.Message.documentation: Documentation? get() = documentation(this.doc)

    val Protocol.json: String get() = this.toString(true)

    val Protocol.Message.requestName: Name get() = Name("${this.name.firstUppercase()}Request")

    fun Protocol.Message.createRecord(namespace: Namespace): AvroSchema = AvroSchema(
      Schema.createRecord(
        this.requestName.value,
        doc,
        namespace.value,
        this.request.isError,
        this.request.fields.map { Schema.Field(it, it.schema()) })
    )

    // TODO: hopefully we will not need this reflection stuff, but if the above createRecord method loses the reference, we might.
    private val schemaFieldPosition = Schema.Field::class.java.getDeclaredField("position").apply { isAccessible = true }
    fun Schema.Field.resetPosition() = schemaFieldPosition.set(this, -1)

    /**
     * Protocol file extension is `avpr`.
     */
    val Protocol.fileExtension: String get() = AvroKotlin.FileExtension.PROTOCOL

    /**
     * Protocol file path based on namespace, name and extension.
     */
    val Protocol.path: Path get() = (Namespace(namespace) + Name(name)).toPath(fileExtension)

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

