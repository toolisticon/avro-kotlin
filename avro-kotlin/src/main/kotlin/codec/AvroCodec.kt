package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.value.BinaryEncodedBytes
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.generic.GenericData
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificData

/**
 * > Avro specifies two serialization encodings: binary and JSON.
 * > Most applications will use the binary encoding, as it is smaller and
 * > faster. But, for debugging and web-based applications, the JSON encoding
 * > may sometimes be appropriate.
 */
object AvroCodec {

  fun specificData(genericData: GenericData): SpecificData = SpecificData().apply {
    genericData.conversions.forEach { this.addLogicalTypeConversion(it) }
  }

  fun genericData(specificData: SpecificData): GenericData = GenericData().apply {
    specificData.conversions.forEach { this.addLogicalTypeConversion(it) }
  }

  internal val encoderFactory: EncoderFactory get() = EncoderFactory.get()
  internal val decoderFactory: DecoderFactory get() = DecoderFactory.get()

  fun interface Converter<SOURCE, TARGET> {
    fun convert(source: SOURCE): TARGET
  }

  fun interface Decoder<ENCODED, TYPE> {
    fun decode(encoded: ENCODED): TYPE
  }

  fun interface Encoder<TYPE, ENCODED> {
    fun encode(type: TYPE): ENCODED
  }

  interface JsonEncoder<TYPE> : Encoder<TYPE, JsonString>
  interface JsonDecoder<TYPE> : Decoder<JsonString, TYPE>

  fun interface SingleObjectEncoder<TYPE> : Encoder<TYPE, SingleObjectEncodedBytes>
  fun interface SingleObjectDecoder<TYPE> : Decoder<SingleObjectEncodedBytes, TYPE>

  interface BinaryEncoder<TYPE> : Encoder<TYPE, BinaryEncodedBytes>
  interface BinaryDecoder<TYPE> : Decoder<BinaryEncodedBytes, TYPE>

}
