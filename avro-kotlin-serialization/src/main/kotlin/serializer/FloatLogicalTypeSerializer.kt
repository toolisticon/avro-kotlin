package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.avro.kotlin.logical.FloatLogicalType
import io.toolisticon.avro.kotlin.logical.conversion.FloatLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

abstract class FloatLogicalTypeSerializer<LOGICAL : FloatLogicalType, CONVERTED_TYPE : Any>(
  conversion: FloatLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Float, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.FLOAT
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = decoder.decodeFloat()
    return conversion.fromAvro(value)
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeFloat(conversion.toAvro(obj))
  }
}
