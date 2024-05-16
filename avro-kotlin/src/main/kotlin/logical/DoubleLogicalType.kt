package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.DOUBLE], must not require additional properties and should be implemented as singleton.
 */
abstract class DoubleLogicalType(name: LogicalTypeName) : AvroLogicalType<Double>(name, SchemaType.DOUBLE)


/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [DoubleLogicalType] when present.
 */
abstract class DoubleLogicalTypeFactory<T : DoubleLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, Double>(logicalType)
