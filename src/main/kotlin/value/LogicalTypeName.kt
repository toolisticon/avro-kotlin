package io.toolisticon.avro.kotlin.value

import org.apache.avro.LogicalType

@JvmInline
value class LogicalTypeName(override val value: String) : ValueType<String> {
  companion object {
    fun schemaLogicalTypeName(logicalType: LogicalType?) = logicalType?.let { LogicalTypeName(it.name) }

    val DATE = LogicalTypeName("date")

    val DECIMAL = LogicalTypeName("decimal")

    @Deprecated("Duration is not supported by apache-avro-java.")
    val DURATION = LogicalTypeName("duration")

    val LOCAL_TIMESTAMP_MICROS = LogicalTypeName("local-timestamp-micros")

    val LOCAL_TIMESTAMP_MILLIS = LogicalTypeName("local-timestamp-millis")

    val TIME_MICROS = LogicalTypeName("time-micros")

    val TIME_MILLIS = LogicalTypeName("time-millis")

    val TIMESTAMP_MICROS = LogicalTypeName("timestamp-micros")

    val TIMESTAMP_MILLIS = LogicalTypeName("timestamp-millis")

    val UUID = LogicalTypeName("uuid")
  }
}
