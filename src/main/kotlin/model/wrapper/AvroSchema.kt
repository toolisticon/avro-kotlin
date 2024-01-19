package io.toolisticon.avro.kotlin.model.wrapper

import io.toolisticon.avro.kotlin.AvroKotlin.documentation
import io.toolisticon.avro.kotlin.AvroKotlin.orEmpty
import io.toolisticon.avro.kotlin.model.AvroTypesMap
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.SchemaType.Companion.PRIMITIVE_TYPES
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.inputStream

/**
 * A kotlin type- and null-safe wrapper around the java [Schema].
 */
class AvroSchema(
  /**
   * The original [Schema] wrapped by this class. Accessible via #get.
   */
  private val schema: Schema,

  /**
   * Marks the root record type, in case this is the record type that represents the parsed schema.
   *
   * We need this information in code generation to treat root types differently.
   */
  val isRoot: Boolean = false,

  /**
   * The [Name] of the schema. Though every schema must have a name,
   * in case of protocol message requests, this is not the case, in these cases, we have to provide the
   * name via constructor.
   */
  override val name: Name = Name(schema),
) : SchemaSupplier, WithObjectProperties {
  companion object {
    private fun create(inputStream: InputStream, isRoot: Boolean = false, name: Name? = null) = with(Schema.Parser().parse(inputStream)) {
      AvroSchema(schema = this, name = name ?: Name(this), isRoot = isRoot)
    }

    operator fun invoke(json: JsonString, isRoot: Boolean = false, name: Name? = null): AvroSchema = create(json.inputStream(), isRoot, name)
    operator fun invoke(file: File): AvroSchema = create(file.inputStream(), isRoot = true)
    operator fun invoke(path: Path): AvroSchema = create(path.inputStream(), isRoot = true)
    operator fun invoke(resource: URL): AvroSchema = create(resource.openStream(), isRoot = true)

    const val FILE_EXTENSION = "avsc"
  }

  /**
   * The [AvroHashCode] representing the [Schema]. This hash contains additional information like logicalType or documentation.
   */
  override val hashCode: AvroHashCode = AvroHashCode(schema)

  /**
   * The fingerprint of the [Schema], used to lookup in schema store.
   *
   * This fingerprint does not contain information like logicalType and documentation.
   */
  val fingerprint: AvroFingerprint = AvroFingerprint(schema)

  /**
   * This is only possible for named types: FIXED, ENUM, RECORD.
   * In all other cases this is empty.
   */
  val aliases: Set<String> by lazy {
    runCatching { schema.aliases }.getOrDefault(emptySet())
  }

  /**
   * The content of the `doc` field.
   */
  val documentation: Documentation? by lazy { documentation(schema) }

  // FIXME: fails with NPE when not lazy. Remove?
  val fullName: String by lazy {
    schema.fullName
  }

  override val json: JsonString by lazy { JsonString(schema) }

  val namespace: Namespace by lazy {
    Namespace(schema)
  }

  /**
   * The type of this schema, corresponds to [Schema.Type].
   */
  val type: SchemaType = SchemaType.valueOfType(schema.type)

  /**
   * The full name of the schema, concats [namespace] and [name].
   */
  val canonicalName = namespace + name

  /**
   * Additional properties, defaults to [ObjectProperties.EMPTY].
   */
  override val properties: ObjectProperties = ObjectProperties(schema)

  /**
   * `true` if [properties] is not empty.
   */
  val hasProps: Boolean = properties.isNotEmpty()

  /**
   * Does this schema contain an additional property with given key?
   *
   * @param key the property key to look for
   * «return `true` if [properties] contains given key, `false` else.
   */
  fun hasProp(key: String) = properties.containsKey(key)

  /**
   * If this is a UNION, this contains the types of the union.
   * In this case, this must not be empty.
   *
   * For all other types, this is empty.
   *
   * @see Schema#getTypes
   */
  val unionTypes: List<AvroSchema> = runCatching { schema.types?.map { AvroSchema(it) } }.getOrNull() ?: emptyList()

  override fun equals(other: Any?): Boolean = hashCode.value == other?.hashCode()
  override fun hashCode(): Int = hashCode.value
  override fun toString(): String = toString(false)
  fun toString(pretty: Boolean): String = schema.toString(pretty)

  val logicalType: LogicalType? = schema.logicalType

  val hasLogicalType: Boolean = logicalType != null

  /**
   * If this is a [SchemaType.RECORD], get the field for given name.
   *
   * @param fieldname the field to get
   * @return if fieldname exists in record -> the field, else: `null`
   */
  fun getField(fieldname: Name): AvroSchemaField? = fields.find { it.name == fieldname }

  /**
   * If this is a [SchemaType.RECORD], contains the fields of the schema.
   * In this case, this must not be empty.
   *
   * For all other types, this is empty.
   */
  val fields: List<AvroSchemaField> by lazy {
    runCatching { schema.fields.map { AvroSchemaField(it) } }.orEmpty()
  }

  /**
   * Only valid if this is of type ENUM, then it must not be empty.
   * In all other cases, this is empty.
   */
  val enumSymbols: List<String> = runCatching { schema.enumSymbols }.getOrNull() ?: emptyList()

  /**
   * Only valid if this is of type ARRAY, then it must not be null.
   * In all other cases, this is null.
   */
  val arrayType: AvroSchema? = runCatching { schema.elementType?.let { AvroSchema(it) } }.getOrNull()

  /**
   * Only valid if this is of type MAP, then it must not be null.
   * In all other cases, this is null.
   */
  val mapType: AvroSchema? = runCatching {
    schema.valueType?.let { AvroSchema(it) }
  }.getOrNull()

  val enclosedTypes: List<AvroSchema> by lazy {
    buildList {
      addAll(unionTypes)
      fields.map(AvroSchemaField::schema).forEach { fieldSchema ->
        if (this.find { it.hashCode == fieldSchema.hashCode } == null) {
          add(fieldSchema)
        }
      }
      mapType?.also { add(it) }
      arrayType?.also { add(it) }
    }
  }

  fun getIndexNamed(name: String): Int = schema.getIndexNamed(name)

  fun getFixedSize(): Int = schema.fixedSize

  val isArrayType: Boolean = SchemaType.ARRAY == type && arrayType != null

  val isBooleanType: Boolean = SchemaType.BOOLEAN == type

  val isBytesType: Boolean = SchemaType.BYTES == type

  val isDoubleType: Boolean = SchemaType.DOUBLE == type

  val isEnumType: Boolean = SchemaType.ENUM == type

  val isError = runCatching { schema.isError }.getOrDefault(false)
  val isErrorType: Boolean = SchemaType.RECORD == type && isError

  val isFloatType: Boolean = SchemaType.FLOAT == type

  val isIntType: Boolean = SchemaType.INT == type

  val isLongType: Boolean = SchemaType.LONG == type

  val isMapType: Boolean = SchemaType.MAP == type

  val isNullable: Boolean = schema.isNullable
  val isNullType: Boolean = SchemaType.NULL == type

  val isPrimitive: Boolean = PRIMITIVE_TYPES.contains(type)

  val isRecordType: Boolean = SchemaType.RECORD == type && !isError

  val isStringType: Boolean = SchemaType.STRING == type

  val isUnion: Boolean = schema.isUnion
  val isUnionType: Boolean = SchemaType.UNION == type && isUnion && unionTypes.isNotEmpty()

  val typesMap: AvroTypesMap get() = AvroTypesMap(this)

  init {
    if (isRoot) {
      require(type.isNamed) { "Only NamedTypes must be marked as 'isRoot'." }
      requireNotNull(schema.namespace) { "A top level schema declaration must have a namespace." }
    }
  }

  override fun get(): Schema = schema

}
