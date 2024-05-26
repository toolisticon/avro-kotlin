package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.StringLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

abstract class StringLogicalTypeSerializer<LOGICAL : StringLogicalType, CONVERTED_TYPE : Any>(
  conversion: StringLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, String, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.STRING
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val stringValue = decoder.decodeString()
    return conversion.fromAvro(stringValue)
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeString(conversion.toAvro(obj))
  }
}
