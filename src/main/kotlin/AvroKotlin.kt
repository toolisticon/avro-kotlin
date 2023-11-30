package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.AvroKotlin.Constants.PRIMITIVE_TYPES
import io.toolisticon.avro.kotlin.AvroKotlin.Constants.SCHEMA_RESERVED_KEYWORDS
import io.toolisticon.avro.kotlin._bak.*
import io.toolisticon.avro.kotlin.key.AvroFingerprint
import io.toolisticon.avro.kotlin.key.AvroHashCode
import io.toolisticon.avro.kotlin.ktx.trimToNull
import io.toolisticon.avro.kotlin.model.*
import io.toolisticon.avro.kotlin.name.LogicalTypeName
import io.toolisticon.avro.kotlin.name.Name
import io.toolisticon.avro.kotlin.name.Namespace
import org.apache.avro.*
import org.apache.avro.Schema.Type
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.streams.asSequence

object AvroKotlin {

  object Constants {
    /**
     * Default separator used in canonical name.
     */
    const val NAME_SEPARATOR = "."


    /**
     * Marker bytes according to Avro schema specification v1.
     */
    @JvmField
    val AVRO_V1_HEADER = byteArrayOf(-61, 1) // [C3 01]

    /**
     * Length of default avro header bytes.
     */
    val AVRO_HEADER_LENGTH = AVRO_V1_HEADER.size + Long.SIZE_BYTES

    /**
     * Avro Schema files end with `.avsc`.
     */
    const val EXTENSION_SCHEMA: FileExtension = "avsc"

    /**
     * Avro protocol files end with `.avpr`.
     */
    const val EXTENSION_PROTOCOL: FileExtension = "avpr"

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


  var verifyPackageConvention = true

  fun schema(namespace: Namespace, name: Name): SchemaFqn = SchemaFqn(namespace = namespace, name = name)
  fun schema(fqn: AvroFqn): SchemaFqn = schema(namespace = fqn.namespace, name = fqn.name)

  fun protocol(namespace: Namespace, name: Name): ProtocolFqn = ProtocolFqn(namespace = namespace, name = name)
  fun protocol(fqn: AvroFqn): ProtocolFqn = protocol(namespace = fqn.namespace, name = fqn.name)


  /**
   * Create [AvroFqn] based on namespace and name.
   */
  fun fqn(namespace: Namespace, name: Name): AvroFqn = AvroFqnData(namespace = namespace, name = name)

  fun findDeclarations(root: Path, filter: (Path) -> Boolean = Declaration.BOTH): List<Any> {
    val fqns = Files.walk(root)
      .asSequence()
      .filter { it.isRegularFile() }
      .filter(filter)
      .map { GenericAvroDeclarationFqn.fromPath(it, root) }
      .toList()

    return fqns.map { fqn ->
      if (fqn.fileExtension == Declaration.SCHEMA.fileExtension) {
        SchemaDeclaration(
          location = fqn,
          content = schema(fqn).fromDirectory(root.toFile())
        )
      } else if (fqn.fileExtension == Declaration.PROTOCOL.fileExtension) {
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

  enum class Declaration(val fileExtension: FileExtension) {
    SCHEMA(Constants.EXTENSION_SCHEMA),
    PROTOCOL(Constants.EXTENSION_PROTOCOL)
    ;

    companion object {
      private val extensions = Declaration.entries.associateBy { it.fileExtension }

      val BOTH: (Path) -> Boolean = { setOf(SCHEMA.fileExtension, PROTOCOL.fileExtension).contains(it.extension) }

      fun byExtension(fileExtension: FileExtension) = requireNotNull(extensions[fileExtension]) { "illegal file extension='$fileExtension'" }
    }
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
  fun name(schema: Schema) = io.toolisticon.avro.kotlin.name.Name(schema.name)

  /**
   * Get [Name] for given [Schema.Field].
   */
  fun name(field: Schema.Field) = io.toolisticon.avro.kotlin.name.Name(field.name())

  /**
   * Get [Name] from given [Protocol].
   */
  fun name(protocol: Protocol) = io.toolisticon.avro.kotlin.name.Name(protocol.name)

  fun namespace(schema: Schema): io.toolisticon.avro.kotlin.name.Namespace? {
    return try {
      schema.namespace?.let { io.toolisticon.avro.kotlin.name.Namespace(it) }
    } catch (e: AvroRuntimeException) {
      null
    }
  }

  fun namespace(protocol: Protocol) = io.toolisticon.avro.kotlin.name.Namespace(
    requireNotNull(protocol.namespace) { "protocol must have a namespace" }
  )

  fun logicalTypeName(logicalType: LogicalType?) = logicalType?.let { LogicalTypeName(it.name) }

  /**
   * Create a wrapped primitive Schema based on type.
   *
   * @param type the schema type
   * @param objectProperties the additional properties, defaults to EMPTY
   * @return wrapped primitive schema
   */
  fun primitiveSchema(
    type: Type,
    objectProperties: ObjectProperties = ObjectProperties.EMPTY
  ): AvroSchema = AvroSchema(Schema.create(type).apply {
    objectProperties.properties.entries.forEach { (k, v) ->
      addProp(k, v)
    }
  })

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
