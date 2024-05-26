package io.toolisticon.kotlin.avro.logical

import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.BYTES], must not require additional properties and should be implemented as singleton.
 */
abstract class BytesLogicalType(name: LogicalTypeName) : AvroLogicalType<ByteArray>(name, SchemaType.BYTES)


/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [BytesLogicalType] when present.
 */
abstract class BytesLogicalTypeFactory<T : BytesLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, ByteArray>(logicalType)
