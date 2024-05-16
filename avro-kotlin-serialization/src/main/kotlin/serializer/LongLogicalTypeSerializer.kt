package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.avro.kotlin.logical.DoubleLogicalType
import io.toolisticon.avro.kotlin.logical.IntLogicalType
import io.toolisticon.avro.kotlin.logical.LongLogicalType
import io.toolisticon.avro.kotlin.logical.conversion.DoubleLogicalTypeConversion
import io.toolisticon.avro.kotlin.logical.conversion.IntLogicalTypeConversion
import io.toolisticon.avro.kotlin.logical.conversion.LongLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

abstract class LongLogicalTypeSerializer<LOGICAL : LongLogicalType, CONVERTED_TYPE : Any>(
  conversion: LongLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Long, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.LONG
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = decoder.decodeLong()
    return conversion.fromAvro(value)
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    return encoder.encodeLong(conversion.toAvro(obj))
  }
}
