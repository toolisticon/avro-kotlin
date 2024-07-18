package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.IntLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.IntLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

@OptIn(ExperimentalSerializationApi::class)
abstract class IntLogicalTypeSerializer<LOGICAL : IntLogicalType, CONVERTED_TYPE : Any>(
  conversion: IntLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Int, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.INT
) {

  override fun serializeAvro(encoder: AvroEncoder, value: CONVERTED_TYPE) {
    encoder.encodeInt(conversion.toAvro(value))
  }

  override fun deserializeAvro(decoder: AvroDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeValue()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when(value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeInt())
    }
  }
}
