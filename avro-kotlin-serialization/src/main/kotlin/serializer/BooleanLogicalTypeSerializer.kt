package io.toolisticon.kotlin.avro.serialization.serializer


import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import io.toolisticon.kotlin.avro.logical.BooleanLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.BooleanLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind

@OptIn(ExperimentalSerializationApi::class)
abstract class BooleanLogicalTypeSerializer<LOGICAL : BooleanLogicalType, CONVERTED_TYPE : Any>(
  conversion: BooleanLogicalTypeConversion<LOGICAL, CONVERTED_TYPE>
) : AvroLogicalTypeSerializer<LOGICAL, Boolean, CONVERTED_TYPE>(
  conversion = conversion,
  primitiveKind = PrimitiveKind.BOOLEAN,
  encode = AvroEncoder::encodeBoolean,
  decode = AvroDecoder::decodeBoolean
)
