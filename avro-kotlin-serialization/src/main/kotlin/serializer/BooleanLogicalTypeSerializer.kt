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
    val value = decoder.decodeBoolean()
    return conversion.fromAvro(value)
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeBoolean(conversion.toAvro(obj))
  }
}
