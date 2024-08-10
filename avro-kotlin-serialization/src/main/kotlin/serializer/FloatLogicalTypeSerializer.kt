package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.FloatLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.FloatLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class)
abstract class FloatLogicalTypeSerializer<LOGICAL : FloatLogicalType, CONVERTED_TYPE : Any>(
  conversion: FloatLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Float, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.FLOAT,
  encode = AvroEncoder::encodeFloat,
  decode = AvroDecoder::decodeFloat
)
