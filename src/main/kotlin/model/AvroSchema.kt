package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin.Constants.PRIMITIVE_TYPES
import io.toolisticon.avro.kotlin.AvroKotlin.documentation
import io.toolisticon.avro.kotlin.AvroKotlin.name
import io.toolisticon.avro.kotlin.AvroKotlin.namespace
import io.toolisticon.avro.kotlin.ktx.orEmpty
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import org.apache.avro.Schema.Type
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.function.Supplier

/**
 * A kotlin type- and null-safe wrapper around the java [Schema].
 */
class AvroSchema(
  /**
   * The original [Schema] wrapped by this class. Accessible via #get.
   */
  val schema: Schema,

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
  val name: Name = name(schema),
) : Supplier<Schema> {
  companion object {
    private fun create(inputStream: InputStream, isRoot: Boolean = false, name: Name? = null) = with(Schema.Parser().parse(inputStream)) {
      AvroSchema(schema = this, name = name ?: name(this), isRoot = isRoot)
    }

    operator fun invoke(json: JsonString, isRoot: Boolean = false, name: Name? = null): AvroSchema = create(json.inputStream(), isRoot, name)
    operator fun invoke(file: File): AvroSchema = create(file.inputStream(), isRoot = true)
    operator fun invoke(resource: URL): AvroSchema = create(resource.openStream(), isRoot = true)

  }

  /**
   * The hashCode identifies a [Schema].
   */
  val hashCode: AvroHashCode = AvroHashCode(schema)

  /**
   * The fingerprint of the [Schema], used to lookup in schema store.
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

  val fullName: String = schema.fullName

  val json: JsonString = JsonString(schema)

  val namespace: Namespace? by lazy {
    namespace(schema)
  }
  val type: Type = schema.type

  val objectProps: ObjectProperties = ObjectProperties(schema)
  val hasProps: Boolean = objectProps.isNotEmpty()
  fun hasProp(key: String) = objectProps.containsKey(key)

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

  fun getField(fieldname: Name): AvroSchemaField? = fields.find { it.name == fieldname }

  val fields: List<AvroSchemaField> by lazy {
    runCatching { schema.fields.map { AvroSchemaField(it) } }.orEmpty()
  }
  val hasFields: Boolean by lazy { fields.isNotEmpty() }

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

  fun getIndexNamed(name: String): Int = schema.getIndexNamed(name)

  fun getFixedSize(): Int = schema.fixedSize

  val isArrayType: Boolean = Type.ARRAY == type && arrayType != null
  val isBooleanType: Boolean = Type.BOOLEAN == type
  val isBytesType: Boolean = Type.BYTES == type
  val isDoubleType: Boolean = Type.DOUBLE == type
  val isEnumType: Boolean = Type.ENUM == type
  val isError: Boolean = runCatching { schema.isError }.getOrDefault(false)
  val isFloatType: Boolean = Type.FLOAT == type
  val isIntType: Boolean = Type.INT == type
  val isLongType: Boolean = Type.LONG == type
  val isMapType: Boolean = Type.MAP == type
  val isNullType: Boolean = Type.NULL == type
  val isNullable: Boolean = schema.isNullable
  val isPrimitive: Boolean = PRIMITIVE_TYPES.contains(type)
  val isRecordType: Boolean = Type.RECORD == type
  val isStringType: Boolean = Type.STRING == type
  val isUnion: Boolean = schema.isUnion
  val isUnionType: Boolean = Type.UNION == type && isUnion && unionTypes.isNotEmpty()

  init {
    if (isRoot) {
      require(isRecordType && !isError) { "Only RecordTypes that are not ErrorTypes must be marked as 'isRoot'." }
    }
  }

  override fun get(): Schema = schema

  fun withLogicalType(logicalType: LogicalType) = AvroSchema(
    schema = logicalType.addToSchema(schema),
    name = name,
    isRoot = isRoot
  )
}

fun AvroSchema.enclosedTypes(): List<AvroSchema> = buildList {
  addAll(unionTypes)
  fields.map(AvroSchemaField::schema).forEach { fieldSchema ->
    if (this.find { it.hashCode == fieldSchema.hashCode } == null) {
      add(fieldSchema)
    }
  }
  mapType?.also { add(it) }
  arrayType?.also { add(it) }
}


fun AvroSchema.recordType(): RecordType = RecordType(this)
fun AvroSchema.enumType(): EnumType = EnumType(this)

fun AvroSchema.arrayType(): ArrayType = ArrayType(this)
fun AvroSchema.unionType(): UnionType = UnionType(this)
fun AvroSchema.mapType(): MapType = MapType(this)

fun AvroSchema.booleanType() = BooleanType(this)
fun AvroSchema.bytesType() = BytesType(this)
fun AvroSchema.doubleType() = DoubleType(this)
fun AvroSchema.floatType() = FloatType(this)
fun AvroSchema.intType() = IntType(this)
fun AvroSchema.longType() = LongType(this)
fun AvroSchema.nullType() = NullType
fun AvroSchema.stringType() = StringType(this)

fun AvroSchema.primitiveType() = when (type) {
  Type.BOOLEAN -> booleanType()
  Type.BYTES -> bytesType()
  Type.DOUBLE -> doubleType()
  Type.FLOAT -> floatType()
  Type.INT -> intType()
  Type.LONG -> longType()
  Type.NULL -> NullType
  Type.STRING -> stringType()
  else -> throw IllegalArgumentException("Type must be primitive: $PRIMITIVE_TYPES")
}

fun AvroSchema.avroType(): AvroType = when (type) {
  // Named
  Type.RECORD -> recordType()
  Type.ENUM -> enumType()
  // FIXME: support FIXED
  Type.FIXED -> TODO("fixed not supported")

  // Container
  Type.ARRAY -> arrayType()
  Type.MAP -> mapType()
  Type.UNION -> unionType()

  // Primitive
  else -> primitiveType()
}
