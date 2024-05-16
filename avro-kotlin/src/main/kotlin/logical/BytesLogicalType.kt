package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.BYTES], must not require additional properties and should be implemented as singleton.
 */
abstract class BytesLogicalType(name: LogicalTypeName) : AvroLogicalType<ByteArray>(name, SchemaType.BYTES)


/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [BytesLogicalType] when present.
 */
abstract class BytesLogicalTypeFactory<T : BytesLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, ByteArray>(logicalType)
