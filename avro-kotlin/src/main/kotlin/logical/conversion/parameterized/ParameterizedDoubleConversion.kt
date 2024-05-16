package io.toolisticon.avro.kotlin.logical.conversion.parameterized

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
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
    schema = AvroSchema(schema),
    logicalType = type
  )

  override fun toDouble(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?) = toAvro(
    value = value,
    schema = AvroSchema(schema),
    logicalType = type
  )
}
