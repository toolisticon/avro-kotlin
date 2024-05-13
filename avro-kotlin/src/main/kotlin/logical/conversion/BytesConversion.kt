package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import java.nio.ByteBuffer

abstract class BytesConversion<CONVERTED_TYPE>(
  logicalTypeName: LogicalTypeName,
  convertedType: Class<CONVERTED_TYPE>
) : PrimitiveTypeConversion<CONVERTED_TYPE, ByteArray, ByteBuffer>(
  logicalTypeName = logicalTypeName,
  convertedType = convertedType,
  primitiveType = SchemaType.BYTES
) {

  override fun fromBytes(value: ByteBuffer, schema: Schema?, type: LogicalType?) = fromAvro(
    value = value.array(),
    schema = AvroSchema(schema),
    logicalType = type
  )

  override fun toBytes(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?) = ByteBuffer.wrap(
    toAvro(
      value = value,
      schema = AvroSchema(schema),
      logicalType = type
    )
  )
}
