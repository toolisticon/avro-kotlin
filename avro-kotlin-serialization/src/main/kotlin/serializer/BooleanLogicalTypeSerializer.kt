package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.kotlin.avro.logical.BooleanLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.BooleanLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

abstract class BooleanLogicalTypeSerializer<LOGICAL : BooleanLogicalType, CONVERTED_TYPE : Any>(
  conversion: BooleanLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Boolean, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.BOOLEAN
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeAny()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when(value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      else -> conversion.fromAvro(decoder.decodeBoolean())
    }
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeBoolean(conversion.toAvro(obj))
  }
}
