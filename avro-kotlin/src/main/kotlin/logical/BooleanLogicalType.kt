package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.BOOLEAN], must not require additional properties and should be implemented as singleton.
 */
abstract class BooleanLogicalType(name: LogicalTypeName) : AvroLogicalType<Boolean>(name, SchemaType.BOOLEAN)


/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [BooleanLogicalType] when present.
 */
abstract class BooleanLogicalTypeFactory<T : BooleanLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, Boolean>(logicalType)
