package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.kotlin.avro.logical.IntLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.IntLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

abstract class IntLogicalTypeSerializer<LOGICAL : IntLogicalType, CONVERTED_TYPE : Any>(
  conversion: IntLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Int, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.INT
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeAny()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when(value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeInt())
    }
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeInt(conversion.toAvro(obj))
  }
}
