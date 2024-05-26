package io.toolisticon.kotlin.avro.logical

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.value.property.LogicalTypeNameProperty
import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes.LogicalTypeFactory
import org.apache.avro.Schema

/**
 * We need a [LogicalTypeFactory] to register custom types. It is a java avro api
 * requirement.
 *
 * As the [AvroLogicalType] is de facto a singleton instance,
 * we do not really need the factory, though. We just need a "something"
 * that returns our singleton to fulfill a contract.
 *
 */
sealed class AvroLogicalTypeFactory<
  T : AvroLogicalType<JVM_TYPE>,
  JVM_TYPE : Any>(
  val logicalType: T
) : LogicalTypeFactory {
  override fun toString(): String = toString(this::class.java.simpleName) {
    add(property = "name", value = logicalType.name, wrap = "'")
    add(property = "type", value = logicalType.type)
  }

  override fun getTypeName(): String = logicalType.getName()

  // check if logical type derived from given schema matches the expected singleton type
  override fun fromSchema(schema: Schema?): LogicalType? = if (LogicalTypeNameProperty.from(schema)?.value == logicalType.name) {
    logicalType
  } else {
    null
  }
}
