package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.STRING], must not require additional properties and should be implemented as singleton.
 */
abstract class StringLogicalType(name: LogicalTypeName) : AvroLogicalType<String>(name, SchemaType.STRING)

/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [StringLogicalType] when present.
 */
abstract class StringLogicalTypeFactory<T : StringLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, String>(logicalType)
