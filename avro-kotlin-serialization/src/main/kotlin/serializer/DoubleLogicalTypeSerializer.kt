package io.toolisticon.kotlin.avro.serialization.serializer

import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.DoubleLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.DoubleLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class)
abstract class DoubleLogicalTypeSerializer<LOGICAL : DoubleLogicalType, CONVERTED_TYPE : Any>(
  conversion: DoubleLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Double, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.DOUBLE,
  encode = AvroEncoder::encodeDouble,
  decode = AvroDecoder::decodeDouble
)
