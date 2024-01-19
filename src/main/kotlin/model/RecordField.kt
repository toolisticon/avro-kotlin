package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaField
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.*

/**
 * Used by [ErrorType] and [RecordType] to encapsulate [org.apache.avro.Schema.Field].
 */
@JvmInline
value class RecordField(private val schemaField: AvroSchemaField) : AvroType, WithDocumentation, SchemaSupplier by schemaField {
  override val schema: AvroSchema get() = schemaField.schema
  override val name: Name get() = schemaField.name
  override val hashCode: AvroHashCode get() = schemaField.schema.hashCode
  override val fingerprint: AvroFingerprint get() = schemaField.schema.fingerprint
  override val properties: ObjectProperties get() = schema.properties

  val type: AvroType get() = AvroType.avroType(schema)

  override val documentation: Documentation? get() = schemaField.documentation
}
