package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.kotlin.avro.logical.LongLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.LongLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

abstract class LongLogicalTypeSerializer<LOGICAL : LongLogicalType, CONVERTED_TYPE : Any>(
  conversion: LongLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Long, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.LONG
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeAny()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when(value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeLong())
    }
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeLong(conversion.toAvro(obj))
  }
}
