package io.toolisticon.avro.kotlin.value

import org.apache.avro.LogicalType

@JvmInline
value class LogicalTypeName(override val value: String) : ValueType<String> {
  companion object {
    fun schemaLogicalTypeName(logicalType: LogicalType?) = logicalType?.let { LogicalTypeName(it.name) }

    val UUID = LogicalTypeName("uuid")
    val DECIMAL = LogicalTypeName("decimal")
    val TIMESTAMP_MICROS = LogicalTypeName("timestamp-micros")
  }

}
