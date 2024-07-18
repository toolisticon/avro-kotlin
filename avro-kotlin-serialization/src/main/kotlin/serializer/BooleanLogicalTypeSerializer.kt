package io.toolisticon.kotlin.avro.serialization.serializer


import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.BooleanLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.BooleanLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.apache.avro.Schema

@OptIn(ExperimentalSerializationApi::class)
abstract class BooleanLogicalTypeSerializer<LOGICAL : BooleanLogicalType, CONVERTED_TYPE : Any>(
  conversion: BooleanLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Boolean, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.BOOLEAN
) {

  override fun deserializeAvro(decoder: AvroDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeValue()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when(value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeBoolean())
    }
  }

  override fun serializeAvro(encoder: AvroEncoder, value: CONVERTED_TYPE) {
    return encoder.encodeBoolean(conversion.toAvro(value))
  }
}
