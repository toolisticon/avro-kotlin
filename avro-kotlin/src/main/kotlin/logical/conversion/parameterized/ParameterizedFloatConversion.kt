package io.toolisticon.avro.kotlin.logical.conversion.parameterized

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema

abstract class ParameterizedFloatConversion<CONVERTED_TYPE>(
  logicalTypeName: LogicalTypeName,
  convertedType: Class<CONVERTED_TYPE>
) : PrimitiveTypeConversion<CONVERTED_TYPE, Float>(
  logicalTypeName = logicalTypeName,
  convertedType = convertedType,
  primitiveType = SchemaType.FLOAT
) {

  override fun fromFloat(value: Float, schema: Schema?, type: LogicalType?) = fromAvro(
    value = value,
    schema = AvroSchema(schema),
    logicalType = type
  )

  override fun toFloat(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?) = toAvro(
    value = value,
    schema = AvroSchema(schema),
    logicalType = type
  )
}
