package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.kotlin.avro.logical.DoubleLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.DoubleLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

abstract class DoubleLogicalTypeSerializer<LOGICAL : DoubleLogicalType, CONVERTED_TYPE : Any>(
  conversion: DoubleLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Double, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.DOUBLE
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeAny()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when(value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeDouble())
    }
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeDouble(conversion.toAvro(obj))
  }
}
