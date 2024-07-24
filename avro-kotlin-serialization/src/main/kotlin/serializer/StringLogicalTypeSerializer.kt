package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.StringLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class)
abstract class StringLogicalTypeSerializer<LOGICAL : StringLogicalType, CONVERTED_TYPE : Any>(
  conversion: StringLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, String, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.STRING
) {

  override fun serializeAvro(encoder: AvroEncoder, value: CONVERTED_TYPE) {
    return encoder.encodeString(conversion.toAvro(value))
  }

  override fun deserializeAvro(decoder: AvroDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeValue()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when (value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeString())
    }
  }
}
