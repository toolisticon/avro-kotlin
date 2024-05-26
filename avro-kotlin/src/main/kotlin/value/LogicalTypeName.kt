package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.value.property.LogicalTypeNameProperty
import org.apache.avro.LogicalType

/**
 * Type-safe wrapper of the string name of a [org.apache.avro.LogicalType].
 */
@JvmInline
value class LogicalTypeName(override val value: String) : ValueType<String> {
  companion object {
    fun String.toLogicalTypeName() = of(this)

    fun of(name: String) = LogicalTypeName(value = name)
    fun ofNullable(name: String?) = name?.toLogicalTypeName()
    fun ofNullable(logicalType: LogicalType?) = logicalType?.name?.toLogicalTypeName()

    fun from(properties: ObjectProperties): LogicalTypeName? = LogicalTypeNameProperty.from(properties)?.value
  }

  override fun toString(): String = value
}
