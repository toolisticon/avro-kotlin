package io.toolisticon.kotlin.avro.logical.conversion.parameterized

import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import java.nio.ByteBuffer

abstract class ParameterizedBytesConversion<CONVERTED_TYPE>(
  logicalTypeName: LogicalTypeName,
  convertedType: Class<CONVERTED_TYPE>
) : PrimitiveTypeConversion<CONVERTED_TYPE, ByteArray>(
  logicalTypeName = logicalTypeName,
  convertedType = convertedType,
  primitiveType = SchemaType.BYTES
) {

  override fun fromBytes(value: ByteBuffer, schema: Schema?, type: LogicalType?) = fromAvro(
    value = value.array(),
    schema = AvroSchema.ofNullable(schema),
    logicalType = type
  )

  override fun toBytes(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?) = ByteBuffer.wrap(
    toAvro(
      value = value,
      schema = AvroSchema.ofNullable(schema),
      logicalType = type
    )
  )
}
