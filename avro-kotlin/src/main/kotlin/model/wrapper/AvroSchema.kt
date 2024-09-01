package io.toolisticon.kotlin.avro.model.wrapper

import io.toolisticon.kotlin.avro.AvroKotlin.formatter
import io.toolisticon.kotlin.avro.AvroKotlin.orEmpty
import io.toolisticon.kotlin.avro.model.EmptyType
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.value.*
import io.toolisticon.kotlin.avro.value.property.LogicalTypeNameProperty
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import org.apache.avro.SchemaCompatibility
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
  override val name: Name = Name.of(schema),
) : SchemaSupplier, WithObjectProperties {

  companion object {
    private fun create(inputStream: InputStream, isRoot: Boolean = false, name: Name? = null) = with(Schema.Parser().parse(inputStream)) {
      AvroSchema(schema = this, name = name ?: Name.of(this), isRoot = isRoot)
    }

    fun of(json: JsonString, isRoot: Boolean = false, name: Name? = null): AvroSchema = create(json.inputStream(), isRoot, name)
    fun of(file: File): AvroSchema = create(file.inputStream(), isRoot = true)
    fun of(path: Path): AvroSchema = create(path.inputStream(), isRoot = true)
    fun of(resource: URL): AvroSchema = create(resource.openStream(), isRoot = true)

    // We might need to be able to continue working in kotlin using a null-safe schema, then we can use this operator.
    fun ofNullable(schema: Schema?): AvroSchema = schema?.let { AvroSchema(it) } ?: EmptyType.schema

    const val FILE_EXTENSION = "avsc"
  }

  /**
   * The [AvroHashCode] representing the [Schema]. This hash contains additional information like logicalType or documentation.
   */
  override val hashCode: AvroHashCode = AvroHashCode.of(schema)

  /**
   * The fingerprint of the [Schema], used to lookup in schema store.
   *
   * This fingerprint does not contain information like logicalType and documentation.
   */
  val fingerprint: AvroFingerprint = AvroFingerprint.of(schema)

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
  val documentation: Documentation? by lazy { Documentation.of(schema) }

  // FIXME: fails with NPE when not lazy. Remove?
  val fullName: String by lazy {
    schema.fullName
  }

  override val json: JsonString by lazy { JsonString.of(schema) }

  val namespace: Namespace by lazy {
    Namespace.of(schema)
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
  override val properties: ObjectProperties = ObjectProperties.ofNullable(schema)

  /**
   * Extract typed Meta properties.
   */
  inline fun <reified META : Any> getMeta(extractor: AvroSchema.() -> META?): META? = this.extractor()

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
  val unionTypes: List<AvroSchema> by lazy {
    runCatching { schema.types?.map { AvroSchema(it) } }.getOrNull() ?: emptyList()
  }

  override fun equals(other: Any?): Boolean = other != null && other is AvroSchema && hashCode.value == other.hashCode()
  override fun hashCode(): Int = hashCode.value
  override fun toString(): String = toString(false)
  fun toString(pretty: Boolean): String = formatter.format(this, pretty)

  val logicalTypeName: LogicalTypeName? = LogicalTypeNameProperty.from(properties)?.value

  val logicalType: LogicalType? = schema.logicalType

  val hasLogicalType: Boolean = logicalType != null

  /**
   * If this is a [SchemaType.RECORD], get the field for given name.
   *
   * @param fieldname the field to get
   * @return if fieldname exists in record -> the field, else: `null`
   */
  fun getField(fieldname: Name): AvroSchemaField? = fields.find { it.name == fieldname }

  fun getField(fieldName: String): AvroSchemaField? = getField(Name(fieldName))

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
  val enumSymbols: List<String> by lazy {
    runCatching { schema.enumSymbols }.getOrNull() ?: emptyList()
  }

  /**
   * Only valid if this is of type ARRAY, then it must not be null.
   * In all other cases, this is null.
   */
  val arrayType: AvroSchema? by lazy {
    runCatching { schema.elementType?.let { AvroSchema(it) } }.getOrNull()
  }

  /**
   * Only valid if this is of type MAP, then it must not be null.
   * In all other cases, this is null.
   */
  val mapType: AvroSchema? by lazy {
    runCatching {
      schema.valueType?.let { AvroSchema(it) }
    }.getOrNull()
  }

  fun getIndexNamed(name: String): Int = schema.getIndexNamed(name)

  fun getFixedSize(): Int = schema.fixedSize

  init {
    if (isRoot) {
      require(type.isNamed) { "Only NamedTypes must be marked as 'isRoot'." }
      requireNotNull(schema.namespace) { "A top level schema declaration must have a namespace." }
    }
  }

  override fun get(): Schema = schema

}

object AvroSchemaChecks {

  val AvroSchema.isArrayType: Boolean get() = SchemaType.ARRAY == type && arrayType != null

  val AvroSchema.isBooleanType: Boolean get() = SchemaType.BOOLEAN == type

  val AvroSchema.isBytesType: Boolean get() = SchemaType.BYTES == type

  val AvroSchema.isDoubleType: Boolean get() = SchemaType.DOUBLE == type

  val AvroSchema.isEnumType: Boolean get() = SchemaType.ENUM == type

  val AvroSchema.isEmptyType: Boolean get() = SchemaType.RECORD == type && fields.isEmpty()
  val AvroSchema.isError: Boolean get() = runCatching { get().isError }.getOrDefault(false)
  val AvroSchema.isErrorType: Boolean get() = SchemaType.RECORD == type && isError

  val AvroSchema.isFloatType: Boolean get() = SchemaType.FLOAT == type

  val AvroSchema.isIntType: Boolean get() = SchemaType.INT == type

  val AvroSchema.isLongType: Boolean get() = SchemaType.LONG == type

  val AvroSchema.isMapType: Boolean get() = SchemaType.MAP == type

  val AvroSchema.isNullable: Boolean get() = get().isNullable
  val AvroSchema.isNullType: Boolean get() = SchemaType.NULL == type

  val AvroSchema.isOptionalType : Boolean get() = isUnion && isNullable &&  get().types?.size == 2 && get().types?.map { it.type }?.any { SchemaType.NULL.get() == it } == true

  val AvroSchema.isPrimitive: Boolean get() = type.isPrimitive

  val AvroSchema.isRecordType: Boolean get() = SchemaType.RECORD == type && !isError

  val AvroSchema.isStringType: Boolean get() = SchemaType.STRING == type

  val AvroSchema.isUnion: Boolean get() = get().isUnion
  val AvroSchema.isUnionType: Boolean get() = SchemaType.UNION == type && isUnion && unionTypes.isNotEmpty()

  /**
   * Check if we can decode using this schema if the encoder used
   * [writer] schema.
   *
   * @param writer - the schema used to encode data
   * @return [AvroSchemaCompatibility] with reader=this
   */
  fun AvroSchema.compatibleToReadFrom(writer: AvroSchema): AvroSchemaCompatibility = AvroSchemaCompatibility(
    value = SchemaCompatibility.checkReaderWriterCompatibility(get(), writer.get())
  )

  /**
   * Check data encoded using this schema could be decoded from [reader] schema.
   *
   * @param reader - the schema to decode the data
   * @return [AvroSchemaCompatibility] with writer=this
   */
  fun AvroSchema.compatibleToBeReadFrom(reader: AvroSchema) = AvroSchemaCompatibility(
    value = SchemaCompatibility.checkReaderWriterCompatibility(reader.get(), get())
  )
}
