package io.toolisticon.avro.kotlin.logical.conversion.parameterized

import io.toolisticon.avro.kotlin.model.EmptyType
import io.toolisticon.avro.kotlin.model.PrimitiveSchemaTypeForLogicalType
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.Conversion
import org.apache.avro.LogicalType
import org.apache.avro.Schema

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
sealed class PrimitiveTypeConversion<CONVERTED_TYPE, JVM_TYPE : Any>(
  private val logicalTypeName: LogicalTypeName,
  private val convertedType: Class<CONVERTED_TYPE>,
  protected val primitiveType: PrimitiveSchemaTypeForLogicalType<JVM_TYPE>
) : Conversion<CONVERTED_TYPE>() {

  val recommendedSchema = primitiveType.schema()

  abstract fun fromAvro(value: JVM_TYPE, schema: AvroSchema = EmptyType.schema, logicalType: LogicalType? = null): CONVERTED_TYPE

  abstract fun toAvro(value: CONVERTED_TYPE, schema: AvroSchema = EmptyType.schema, logicalType: LogicalType? = null): JVM_TYPE

  override fun getConvertedType(): Class<CONVERTED_TYPE> = convertedType


  override fun getLogicalTypeName(): String = logicalTypeName.value

  override fun getRecommendedSchema(): Schema = recommendedSchema.get()
}
