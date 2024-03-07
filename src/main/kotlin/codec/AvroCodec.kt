package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin.logical.AvroLogicalType
import io.toolisticon.avro.kotlin.logical.AvroLogicalTypes
import io.toolisticon.avro.kotlin.logical.BuiltInLogicalType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroFingerprint
import io.toolisticon.avro.kotlin.value.BinaryEncodedBytes
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.SingleObjectEncodedBytes
import org.apache.avro.Conversion
import org.apache.avro.generic.GenericData
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory

/**
 * > Avro specifies two serialization encodings: binary and JSON.
 * > Most applications will use the binary encoding, as it is smaller and
 * > faster. But, for debugging and web-based applications, the JSON encoding
 * > may sometimes be appropriate.
 */
object AvroCodec {
  private val customConversions: List<Conversion<*>> by lazy { AvroLogicalTypes.avroLogicalTypes.map(AvroLogicalType<*>::conversion) }
  private val builtInConversions: List<Conversion<*>> = BuiltInLogicalType.CONVERSIONS

  val defaultGenericData: GenericData by lazy {
    val conversions = customConversions + builtInConversions

    GenericData().apply {
      conversions.forEach { conversion ->
        addLogicalTypeConversion(conversion)
      }
    }
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

  interface SingleObjectEncoder<TYPE> : Encoder<TYPE, SingleObjectEncodedBytes>
  interface SingleObjectDecoder<TYPE> : Decoder<SingleObjectEncodedBytes, TYPE>

  interface BinaryEncoder<TYPE> : Encoder<TYPE, BinaryEncodedBytes>
  interface BinaryDecoder<TYPE> : Decoder<BinaryEncodedBytes, TYPE>

  fun interface AvroSchemaSupplier : (AvroFingerprint) -> AvroSchema {
    operator fun invoke() = invoke(AvroFingerprint.NULL)
  }

}
