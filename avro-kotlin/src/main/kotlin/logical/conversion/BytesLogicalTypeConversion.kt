package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin.logical.BytesLogicalType
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import java.nio.ByteBuffer
import kotlin.reflect.KClass

abstract class BytesLogicalTypeConversion<LOGICAL : BytesLogicalType, CONVERTED_TYPE : Any>(
  logicalType: LOGICAL,
  convertedType: KClass<CONVERTED_TYPE>
) : AvroLogicalTypeConversion<LOGICAL, ByteArray, CONVERTED_TYPE>(
  logicalType = logicalType,
  convertedType = convertedType
) {

  // called by avro internally
  override fun fromBytes(value: ByteBuffer, schema: Schema?, type: LogicalType?): CONVERTED_TYPE {
    validateSchemaAndLogicalType(schema, type)
    return fromAvro(value.array())
  }

  // called by avro internally
  override fun toBytes(value: CONVERTED_TYPE, schema: Schema?, type: LogicalType?): ByteBuffer {
    validateSchemaAndLogicalType(schema, type)
    return ByteBuffer.wrap(toAvro(value))
  }
}
