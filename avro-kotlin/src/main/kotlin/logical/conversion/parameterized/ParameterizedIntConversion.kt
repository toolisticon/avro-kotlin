package io.toolisticon.avro.kotlin.logical.conversion.parameterized

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema

abstract class ParameterizedIntConversion<CONVERTED_TYPE>(
  logicalTypeName: LogicalTypeName,
  convertedType: Class<CONVERTED_TYPE>
) : PrimitiveTypeConversion<CONVERTED_TYPE, Int>(
  logicalTypeName = logicalTypeName,
  convertedType = convertedType,
  primitiveType = SchemaType.INT
) {

  override fun fromInt(value: Int, schema: Schema?, type: LogicalType?) = fromAvro(
    value = value,
    schema = AvroSchema.ofNullable(schema),
    logicalType = type
  )

  override fun toInt(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?) = toAvro(
    value = value,
    schema = AvroSchema.ofNullable(schema),
    logicalType = type
  )
}
