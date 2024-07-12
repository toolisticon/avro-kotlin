package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.StringLogicalTypeConversion
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema
import org.apache.avro.util.Utf8

abstract class StringLogicalTypeSerializer<LOGICAL : StringLogicalType, CONVERTED_TYPE : Any>(
  conversion: StringLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, String, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.STRING
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = requireNotNull(decoder.decodeAny()) { "Can't deserialize null" }
    @Suppress("UNCHECKED_CAST")
    return when(value::class) {
      conversion.convertedType -> value as CONVERTED_TYPE
      Utf8::class -> conversion.fromAvro(decoder.decodeString())
      else -> throw SerializationException("Could not decode $value of type ${value::class}")
    }
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeString(conversion.toAvro(obj))
  }
}
