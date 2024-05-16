package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.avro.kotlin.logical.IntLogicalType
import io.toolisticon.avro.kotlin.logical.conversion.IntLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

abstract class IntLogicalTypeSerializer<LOGICAL : IntLogicalType, CONVERTED_TYPE : Any>(
  conversion: IntLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Int, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.INT
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = decoder.decodeInt()
    return conversion.fromAvro(value)
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeInt(conversion.toAvro(obj))
  }
}
