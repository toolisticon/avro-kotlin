package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.decoder.ExtendedDecoder
import com.github.avrokotlin.avro4k.encoder.ExtendedEncoder
import io.toolisticon.kotlin.avro.logical.BytesLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.BytesLogicalTypeConversion
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema
import java.nio.ByteBuffer

abstract class BytesLogicalTypeSerializer<LOGICAL : BytesLogicalType, CONVERTED_TYPE : Any>(
  conversion: BytesLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, ByteArray, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.BYTE
) {
  override fun decodeAvroValue(schema: Schema, decoder: ExtendedDecoder): CONVERTED_TYPE {
    val value = decoder.decodeAny()
    require(value is ByteArray)
    return conversion.fromAvro(value)
  }

  override fun encodeAvroValue(schema: Schema, encoder: ExtendedEncoder, obj: CONVERTED_TYPE) {
    val buffer = ByteBuffer.wrap(conversion.toAvro(obj))

    return encoder.encodeByteArray(buffer)
  }
}
