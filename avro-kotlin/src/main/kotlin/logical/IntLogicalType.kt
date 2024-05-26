package io.toolisticon.kotlin.avro.logical

import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.INT], must not require additional properties and should be implemented as singleton.
 */
abstract class IntLogicalType(name: LogicalTypeName) : AvroLogicalType<Int>(name, SchemaType.INT)

/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [IntLogicalType] when present.
 */
abstract class IntLogicalTypeFactory<T : IntLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, Int>(logicalType)
