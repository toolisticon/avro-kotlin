package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.kotlin.avro.logical.FloatLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.FloatLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

abstract class FloatLogicalTypeSerializer<LOGICAL : FloatLogicalType, CONVERTED_TYPE : Any>(
  conversion: FloatLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Float, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.FLOAT
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeAny()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when(value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeFloat())
    }
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeFloat(conversion.toAvro(obj))
  }
}
