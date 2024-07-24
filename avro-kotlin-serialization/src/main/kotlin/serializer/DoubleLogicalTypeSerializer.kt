package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.DoubleLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.DoubleLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class)
abstract class DoubleLogicalTypeSerializer<LOGICAL : DoubleLogicalType, CONVERTED_TYPE : Any>(
  conversion: DoubleLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Double, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.DOUBLE
) {


  override fun serializeAvro(encoder: AvroEncoder, value: CONVERTED_TYPE) {
    encoder.encodeDouble(conversion.toAvro(value))
  }

  override fun deserializeAvro(decoder: AvroDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeValue()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when (value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeDouble())
    }
  }
}
