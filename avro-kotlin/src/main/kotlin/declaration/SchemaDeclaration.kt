package io.toolisticon.kotlin.avro.declaration

import io.toolisticon.kotlin.avro.model.AvroTypesMap
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSource
import io.toolisticon.kotlin.avro.value.Documentation.Companion.shortenedIfPresent
import io.toolisticon.kotlin.avro.value.JsonString

/**
 * The result of parsing an `*.avsc` file.
 */
class SchemaDeclaration(
  val schema: AvroSchema,
  override val source: AvroSource,
  private val avroTypesSupplier: () -> AvroTypesMap
) : AvroDeclaration {

  constructor(schema: AvroSchema, source: AvroSource) : this(schema, source, { AvroTypesMap(schema) })

  override val canonicalName = schema.canonicalName

  override val avroTypes: AvroTypesMap by lazy {
    avroTypesSupplier()
  }

  val recordType by lazy {
    RecordType(schema)
  }

  override val documentation = schema.documentation

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

  override fun hashCode(): Int = schema.hashCode()

  init {
    requireNotNull(schema.namespace) { "A top level schema declaration must have a namespace." }
    requireNotNull(schema.name) { "A schema must have a name." }
  }

}
