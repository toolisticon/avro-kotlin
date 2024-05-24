package io.toolisticon.avro.kotlin.value.property

import io.toolisticon.avro.kotlin.value.LogicalTypeName
import io.toolisticon.avro.kotlin.value.LogicalTypeName.Companion.toLogicalTypeName
import io.toolisticon.avro.kotlin.value.ObjectProperties
import io.toolisticon.avro.kotlin.value.ValueType
import org.apache.avro.LogicalType

@JvmInline
value class LogicalTypeNameProperty(override val value: LogicalTypeName) : ValueType<LogicalTypeName>, AvroProperty {

  companion object : AvroPropertySupplier<LogicalTypeNameProperty?> {
    const val PROPERTY_KEY: String = LogicalType.LOGICAL_TYPE_PROP

    @JvmStatic
    override fun from(properties: ObjectProperties): LogicalTypeNameProperty? {
      val value = if(properties.containsKey(PROPERTY_KEY)) {
        properties.getValue<String>(PROPERTY_KEY)
      } else { null }

      return if (value != null) {
        LogicalTypeNameProperty(value.toLogicalTypeName())
      } else {
        null
      }
    }


  }

  override val key: String get() = PROPERTY_KEY

}
