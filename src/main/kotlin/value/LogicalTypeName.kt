package io.toolisticon.avro.kotlin.value

import org.apache.avro.LogicalType

/**
 * Type-safe wrapper of the string name of a [org.apache.avro.LogicalType].
 */
@JvmInline
value class LogicalTypeName(override val value: String) : ValueType<String> {
  companion object {
    fun schemaLogicalTypeName(logicalType: LogicalType?) = logicalType?.let { LogicalTypeName(it.name) }
  }
}
