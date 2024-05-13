package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema

abstract class StringConversion<CONVERTED_TYPE>(
  logicalTypeName: LogicalTypeName,
  convertedType: Class<CONVERTED_TYPE>
) : PrimitiveTypeConversion<CONVERTED_TYPE, String, CharSequence>(
  logicalTypeName = logicalTypeName,
  convertedType = convertedType,
  primitiveType = SchemaType.STRING
) {

  override fun fromCharSequence(value: CharSequence, schema: Schema?, type: LogicalType?) = fromAvro(
    value = value.toString(),
    schema = AvroSchema(schema),
    logicalType = type
  )

  override fun toCharSequence(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?) = toAvro(
    value = value,
    schema = AvroSchema(schema),
    logicalType = type
  ) as CharSequence
}
