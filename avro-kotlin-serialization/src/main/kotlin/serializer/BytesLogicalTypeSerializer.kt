package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.BytesLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.BytesLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import java.nio.ByteBuffer

@OptIn(ExperimentalSerializationApi::class)
abstract class BytesLogicalTypeSerializer<LOGICAL : BytesLogicalType, CONVERTED_TYPE : Any>(
  conversion: BytesLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, ByteArray, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.BYTE
) {


  override fun serializeAvro(encoder: AvroEncoder, value: CONVERTED_TYPE) {
    val buffer = ByteBuffer.wrap(conversion.toAvro(value))
    encoder.encodeBytes(buffer)
  }

  override fun deserializeAvro(decoder: AvroDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeValue()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when (value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      ByteArray::class -> conversion.fromAvro(value as ByteArray)
      else -> throw SerializationException("Can't deserialize $value")
    }
  }
}
