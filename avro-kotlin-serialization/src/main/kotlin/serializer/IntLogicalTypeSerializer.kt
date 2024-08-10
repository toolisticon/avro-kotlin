package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.IntLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.IntLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class)
abstract class IntLogicalTypeSerializer<LOGICAL : IntLogicalType, CONVERTED_TYPE : Any>(
  conversion: IntLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Int, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.INT,
  encode = AvroEncoder::encodeInt,
  decode = AvroDecoder::decodeInt
)
