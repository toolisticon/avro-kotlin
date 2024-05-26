package io.toolisticon.kotlin.avro.logical

import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.value.LogicalTypeName

/**
 * [AvroLogicalType] for [SchemaType.STRING], must not require additional properties and should be implemented as singleton.
 */
abstract class StringLogicalType(name: LogicalTypeName) : AvroLogicalType<String>(name, SchemaType.STRING)

/**
 * [AvroLogicalTypeFactory] that returns the singleton instance of [StringLogicalType] when present.
 */
abstract class StringLogicalTypeFactory<T : StringLogicalType>(logicalType: T) : AvroLogicalTypeFactory<T, String>(logicalType)
