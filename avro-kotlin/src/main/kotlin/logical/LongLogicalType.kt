package io.toolisticon.kotlin.avro.logical

import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.LONG], must not require additional properties and should be implemented as singleton.
 */
abstract class LongLogicalType(name: LogicalTypeName) : AvroLogicalType<Long>(name, SchemaType.LONG)

/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [LongLogicalType] when present.
 */
abstract class LongLogicalTypeFactory<T : LongLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, Long>(logicalType)
