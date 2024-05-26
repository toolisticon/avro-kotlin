package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.*
import org.apache.avro.LogicalType

@JvmInline
value class FixedType(override val schema: AvroSchema) :
  AvroNamedType,
  SchemaSupplier by schema,
  WithDocumentation,
  WithObjectProperties by schema,
  WithLogicalType {

  override val namespace: Namespace get() = schema.namespace
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val properties: ObjectProperties get() = schema.properties
  override val documentation: Documentation? get() = schema.documentation

  override val logicalType: LogicalType? get() = schema.logicalType
  override val logicalTypeName: LogicalTypeName? get() = LogicalTypeName.ofNullable(logicalType = logicalType)

  override fun toString() = toString("FixedType") {
    addIfNotNull("namespace", namespace, "'")
    addIfNotNull("documentation", documentation)
    addIfNotNull("logicalType", logicalTypeName, "'")
    addIfNotEmpty("properties", properties.filterIgnoredKeys())
  }
}
