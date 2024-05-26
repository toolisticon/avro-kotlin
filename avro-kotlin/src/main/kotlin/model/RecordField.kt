package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaField
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.*

/**
 * Used by [ErrorType] and [RecordType] to encapsulate [org.apache.avro.Schema.Field].
 */
@JvmInline
value class RecordField(private val schemaField: AvroSchemaField) : AvroType,
  WithDocumentation,
  WithObjectProperties,
  SchemaSupplier by schemaField {
  override val schema: AvroSchema get() = schemaField.schema
  override val name: Name get() = schemaField.name
  override val hashCode: AvroHashCode get() = schemaField.schema.hashCode
  override val fingerprint: AvroFingerprint get() = schemaField.schema.fingerprint
  override val properties: ObjectProperties get() = schemaField.properties

  val type: AvroType get() = AvroType.avroType(schema)

  override val documentation: Documentation? get() = schemaField.documentation

  override fun toString() = toString("RecordField") {
    add("name", name)
    add("type", type)
    addIfNotNull("documentation", schemaField.documentation)
    addIfNotEmpty("properties", properties.filterIgnoredKeys())
  }
}
