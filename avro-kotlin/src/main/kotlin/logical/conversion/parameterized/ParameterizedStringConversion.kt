package io.toolisticon.avro.kotlin.logical.conversion.parameterized

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema

abstract class ParameterizedStringConversion<CONVERTED_TYPE>(
  logicalTypeName: LogicalTypeName,
  convertedType: Class<CONVERTED_TYPE>
) : PrimitiveTypeConversion<CONVERTED_TYPE, String>(
  logicalTypeName = logicalTypeName,
  convertedType = convertedType,
  primitiveType = SchemaType.STRING
) {

  override fun fromCharSequence(value: CharSequence, schema: Schema?, type: LogicalType?) = fromAvro(
    value = value.toString(),
    schema = AvroSchema.invoke(schema),
    logicalType = type
  )

  override fun toCharSequence(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?) = toAvro(
    value = value,
    schema = AvroSchema(schema),
    logicalType = type
  ) as CharSequence
}
