package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.model.EmptyType
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.Conversion
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import kotlin.reflect.KClass

/**
 * [Conversion] for primitive [io.toolisticon.avro.kotlin.model.SchemaType]s, one-to-one mapping
 * based on converted type and internal jvm type representation.
 *
 * @param <CONVERTED_TYPE> the target type you are converting to (UuidConversion: UUID).
 * @param <CONVERSION> the type used in the conversion methods from/toXXX (UuidConversion: CharSequence)
 * @param <REPRESENTATION> the jvm type used to represent the converted target (UuidConversion: String)
 * @param logicalTypeName the logical type this conversion is responsible for
 * @param convertedType the <TARGET> type as class object, needed to avoid type erasure
 * @param primitiveType the primitive [SchemaType] for which this conversion is valid.
 */
sealed class PrimitiveTypeConversion<CONVERTED_TYPE, REPRESENTATION : Any, CONVERSION : Any>(
  private val logicalTypeName: LogicalTypeName,
  private val convertedType: Class<CONVERTED_TYPE>,
  protected val primitiveType: SchemaType.PrimitiveSchemaType<REPRESENTATION, CONVERSION>
) : Conversion<CONVERTED_TYPE>() {

  val recommendedSchema = primitiveType.schema()

  abstract fun fromAvro(value: REPRESENTATION, schema: AvroSchema = EmptyType.schema, logicalType: LogicalType? = null): CONVERTED_TYPE

  abstract fun toAvro(value: CONVERTED_TYPE, schema: AvroSchema = EmptyType.schema, logicalType: LogicalType? = null): REPRESENTATION

  override fun getConvertedType(): Class<CONVERTED_TYPE> = convertedType

  val conversionType: Class<CONVERSION> get() = primitiveType.conversionType

  override fun getLogicalTypeName(): String = logicalTypeName.value

  override fun getRecommendedSchema(): Schema = recommendedSchema.get()
}
