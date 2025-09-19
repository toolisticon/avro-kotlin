package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import com.github.avrokotlin.avro4k.ExperimentalAvro4kApi
import io.toolisticon.kotlin.avro.logical.BytesLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.BytesLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class, ExperimentalAvro4kApi::class)
abstract class BytesLogicalTypeSerializer<LOGICAL : BytesLogicalType, CONVERTED_TYPE : Any>(
  conversion: BytesLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, ByteArray, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.BYTE,
  encode = AvroEncoder::encodeBytes,
  decode = AvroDecoder::decodeBytes
)
