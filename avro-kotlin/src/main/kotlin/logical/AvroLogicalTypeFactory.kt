package io.toolisticon.avro.kotlin.logical

import _ktx.StringKtx.toString
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes.LogicalTypeFactory
import org.apache.avro.Schema

/**
 * We need a [LogicalTypeFactory] to register custom types. It is an java avro aapi
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

  override fun fromSchema(schema: Schema?): LogicalType? = if (logicalType.name == LogicalTypeName.invoke(schema)) {
    logicalType
  } else {
    null
  }
}
