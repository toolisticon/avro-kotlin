package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.INT], must not require additional properties and should be implemented as singleton.
 */
abstract class IntLogicalType(name: LogicalTypeName) : AvroLogicalType<Int>(name, SchemaType.INT)

/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [IntLogicalType] when present.
 */
abstract class IntLogicalTypeFactory<T : IntLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, Int>(logicalType)
