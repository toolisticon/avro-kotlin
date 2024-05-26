package io.toolisticon.kotlin.avro.logical

import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.FLOAT], must not require additional properties and should be implemented as singleton.
 */
abstract class FloatLogicalType(name: LogicalTypeName) : AvroLogicalType<Float>(name, SchemaType.FLOAT)


/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [FloatLogicalType] when present.
 */
abstract class FloatLogicalTypeFactory<T : FloatLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, Float>(logicalType)
