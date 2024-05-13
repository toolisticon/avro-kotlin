package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.LogicalType
import org.apache.avro.Schema

/**
 * Type-safe wrapper of the string name of a [org.apache.avro.LogicalType].
 */
@JvmInline
value class LogicalTypeName(override val value: String) : ValueType<String> {
  companion object {
    const val PROPERTY_KEY = LogicalType.LOGICAL_TYPE_PROP

    fun String.toLogicalTypeName() = LogicalTypeName(this)

    operator fun invoke(name: String?) = name?.toLogicalTypeName()

    operator fun invoke(schema: Schema?) = schema?.getProp(PROPERTY_KEY)?.toLogicalTypeName()

    operator fun invoke(logicalType: LogicalType?) = logicalType?.name?.toLogicalTypeName()

    operator fun invoke(properties: ObjectProperties): LogicalTypeName? = if(properties.containsKey(PROPERTY_KEY)) {
      properties.getValue<String>(PROPERTY_KEY).toLogicalTypeName()
    } else { null }

    fun schemaLogicalTypeName(logicalType: LogicalType?) = logicalType?.let { LogicalTypeName(it.name) }
  }

  override fun toString(): String = value
}
