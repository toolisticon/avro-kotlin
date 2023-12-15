package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.Schema

@JvmInline
value class RecordField(val schemaField: AvroSchemaField) : AvroType, WithDocumentation, SchemaSupplier {
  override val schema: AvroSchema get() = schemaField.schema
  override val name: Name get() = schemaField.name
  override val hashCode: AvroHashCode get() = schemaField.schema.hashCode
  override val fingerprint: AvroFingerprint get() = schemaField.schema.fingerprint
  override val properties: ObjectProperties get() = schema.properties

  override fun get(): Schema = schemaField.schema.get()
  override val documentation: Documentation? get() = schemaField.documentation
}
