package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.LongLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.LongLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class)
abstract class LongLogicalTypeSerializer<LOGICAL : LongLogicalType, CONVERTED_TYPE : Any>(
  conversion: LongLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Long, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.LONG
) {

  override fun serializeAvro(encoder: AvroEncoder, value: CONVERTED_TYPE) {
    encoder.encodeLong(conversion.toAvro(value))
  }

  override fun deserializeAvro(decoder: AvroDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeValue()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when (value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeLong())
    }
  }
}
