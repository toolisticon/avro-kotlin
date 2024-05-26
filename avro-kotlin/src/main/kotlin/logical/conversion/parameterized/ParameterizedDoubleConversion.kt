package io.toolisticon.kotlin.avro.logical.conversion.parameterized

import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema

abstract class ParameterizedDoubleConversion<CONVERTED_TYPE>(
  logicalTypeName: LogicalTypeName,
  convertedType: Class<CONVERTED_TYPE>
) : PrimitiveTypeConversion<CONVERTED_TYPE, Double>(
  logicalTypeName = logicalTypeName,
  convertedType = convertedType,
  primitiveType = SchemaType.DOUBLE
) {

  override fun fromDouble(value: Double, schema: Schema?, type: LogicalType?) = fromAvro(
    value = value,
    schema = AvroSchema.ofNullable(schema),
    logicalType = type
  )

  override fun toDouble(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?) = toAvro(
    value = value,
    schema = AvroSchema.ofNullable(schema),
    logicalType = type
  )
}
