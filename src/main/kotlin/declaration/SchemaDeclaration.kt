package io.toolisticon.avro.kotlin.declaration

import io.toolisticon.avro.kotlin.model.AvroSchema
import io.toolisticon.avro.kotlin.model.AvroSource
import io.toolisticon.avro.kotlin.model.AvroTypesMap
import io.toolisticon.avro.kotlin.model.RecordType
import io.toolisticon.avro.kotlin.value.Documentation.Companion.shortenedIfPresent
import io.toolisticon.avro.kotlin.value.JsonString

/**
 * The result of parsing an `*.avsc` file.
 */
class SchemaDeclaration(
  val schema: AvroSchema,
  override val source: AvroSource
) : AvroDeclaration {

  override val canonicalName = schema.canonicalName
  override val avroTypes: AvroTypesMap by lazy {
    AvroTypesMap(schema)
  }
  val recordType by lazy {
    RecordType(schema)
  }
  val documentation = schema.documentation
  override val originalJson: JsonString = source.json

  override fun toString() = "SchemaDeclaration(" +
    "namespace='$namespace'" +
    ", name='$name'" +
    documentation.shortenedIfPresent() +
    ", avroTypes=$avroTypes" +
    ", fields=${recordType.fields.map { it.name to it.schema.type }}" +
    ")"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is SchemaDeclaration) return false

    if (schema != other.schema) return false

    return true
  }

  override fun hashCode(): Int {
    return schema.hashCode()
  }

  init {
    requireNotNull(schema.namespace) { "A top level schema declaration must have a namespace." }
    requireNotNull(schema.name) { "A schema must have a name." }
  }

}
