package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.LongLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.LongLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class)
abstract class LongLogicalTypeSerializer<LOGICAL : LongLogicalType, CONVERTED_TYPE : Any>(
  conversion: LongLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Long, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.LONG,
  encode = AvroEncoder::encodeLong,
  decode = AvroDecoder::decodeLong
)
