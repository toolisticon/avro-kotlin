package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.AvroKotlin.Constants.PRIMITIVE_TYPES
import io.toolisticon.avro.kotlin.AvroKotlin.Constants.SCHEMA_RESERVED_KEYWORDS
import io.toolisticon.avro.kotlin._bak.*
import io.toolisticon.avro.kotlin.ktx.trimToNull
import io.toolisticon.avro.kotlin.model.*
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.*
import org.apache.avro.Schema.Type
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

object AvroKotlin {

  /**
   * Encapsulates [Schema#Parser#parse] for the relevant input types.
   */
  object parseSchema {
    operator fun invoke(json: JsonString, isRoot: Boolean = false): AvroSchema = AvroSchema(json, isRoot)
    operator fun invoke(file: File): AvroSchema = AvroSchema(file)
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

  val LOGICALTYPE_EMPTY = object : LogicalType("") {
    override fun addToSchema(schema: Schema): Schema = schema

    override fun validate(schema: Schema) {}
  }


  object Constants {
    /**
     * Default separator used in canonical name.
     */
    const val NAME_SEPARATOR = "."

    /**
     * Marker bytes according to Avro schema specification v1.
     */
    val AVRO_V1_HEADER = SingleObjectEncodedBytes.AVRO_V1_HEADER

    /**
     * Length of default avro header bytes.
     */
    val AVRO_HEADER_LENGTH = SingleObjectEncodedBytes.AVRO_HEADER_LENGTH

    /**
     * Avro Schema files end with `.avsc`.
     */
    const val EXTENSION_SCHEMA: String = "avsc"

    /**
     * Avro protocol files end with `.avpr`.
     */
    const val EXTENSION_PROTOCOL: String = "avpr"

    /**
     * Default charset to use is [Charsets#UTF_8]
     */
    val UTF_8 = Charsets.UTF_8

    /**
     * Set of reserved keywords.
     */
    val SCHEMA_RESERVED_KEYWORDS: Set<String> = setOf("doc", "fields", "items", "name", "namespace", "size", "symbols", "values", "type", "aliases")

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
  }


  fun schema(namespace: Namespace, name: Name): SchemaFqn = SchemaFqn(namespace = namespace, name = name)
  fun schema(fqn: AvroFqn): SchemaFqn = schema(namespace = fqn.namespace, name = fqn.name)

  fun protocol(namespace: Namespace, name: Name): ProtocolFqn = ProtocolFqn(namespace = namespace, name = name)
  fun protocol(fqn: AvroFqn): ProtocolFqn = protocol(namespace = fqn.namespace, name = fqn.name)


  /**
   * Create [AvroFqn] based on namespace and name.
   */
  fun fqn(namespace: Namespace, name: Name): AvroFqn = AvroFqnData(namespace = namespace, name = name)

  fun findDeclarations(root: Path, filter: (Path) -> Boolean = { AvroSpecification.EXTENSIONS.contains(it.extension) }): List<Any> {
    val fqns = Files.walk(root)
      .asSequence()
      .filter { it.isRegularFile() }
      .filter(filter)
      .map { GenericAvroDeclarationFqn.fromPath(it, root) }
      .toList()

    return fqns.map { fqn ->
      if (fqn.fileExtension == AvroSpecification.SCHEMA.value) {
        SchemaDeclaration(
          location = fqn,
          content = schema(fqn).fromDirectory(root.toFile())
        )
      } else if (fqn.fileExtension == AvroSpecification.PROTOCOL.value) {
        protocol(fqn).fromDirectory(root.toFile())
      } else {
        throw IllegalArgumentException("unsupported extension: ${fqn.fileExtension}")
      }
    }
  }

  /**
   * An avro declaration file should have a path that matches its canonicalName, the same way a java file has to
   * be defined in the correct package.
   * @throws AvroDeclarationMismatchException
   */
  @Throws(AvroDeclarationMismatchException::class)
  fun verifyPackagePathConvention(actual: GenericAvroDeclarationFqn, expected: GenericAvroDeclarationFqn): Unit = try {
    require(actual == expected)
  } catch (e: Exception) {
    throw AvroDeclarationMismatchException(actual, expected)
  }


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


  /**
   * Get [Name] from given [Schema].
   */
  fun name(schema: Schema) = Name(schema.name)

  /**
   * Get [Name] for given [Schema.Field].
   */
  fun name(field: Schema.Field) = Name(field.name())

  /**
   * Get [Name] from given [Protocol].
   */
  fun name(protocol: Protocol) = Name(protocol.name)

  fun namespace(schema: Schema): Namespace? {
    return try {
      schema.namespace?.let { Namespace(it) }
    } catch (e: AvroRuntimeException) {
      null
    }
  }

  fun namespace(protocol: Protocol) = Namespace(
    requireNotNull(protocol.namespace) { "protocol must have a namespace" }
  )

  fun logicalTypeName(logicalType: LogicalType?) = logicalType?.let { LogicalTypeName(it.name) }


  fun meta() {
    val c = JsonProperties::class.java.getDeclaredConstructor(Set::class.java).apply {
      isAccessible = true
    }
    //println(JsonProperties::class.java.getDeclaredConstructor(Set::class.java).let { it.name to it.parameters.map { it.type.simpleName } })
    println(c.newInstance(HashSet(SCHEMA_RESERVED_KEYWORDS)))
  }


  /**
   * Pre-generated [Schema]s for primitive types.
   */
  object PRIMITIVE_SCHEMAS {
    private val map = PRIMITIVE_TYPES.map { Schema.create(it) }.map { it }.associateBy { it.type }

    operator fun get(type: Type): Schema = requireNotNull(map[type]) { "not a primitive type: $type" }

    fun hashCode(type: Type) = AvroHashCode(get(type))

    fun fingerprint(type: Type) = AvroFingerprint(get(type))
  }

}
