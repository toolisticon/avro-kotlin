package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.StringLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class)
abstract class StringLogicalTypeSerializer<LOGICAL : StringLogicalType, CONVERTED_TYPE : Any>(
  conversion: StringLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, String, CONVERTED_TYPE>(
  conversion = conversion, primitiveKind = PrimitiveKind.STRING, encode = AvroEncoder::encodeString, decode = AvroDecoder::decodeString
)
