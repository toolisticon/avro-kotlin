package io.toolisticon.avro.kotlin.model.wrapper

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.WithDocumentation
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaChecks.isNullable
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.Schema

class AvroSchemaField private constructor(
  private val field: Schema.Field,
  val schema: AvroSchema,
) : SchemaSupplier by schema,
  WithDocumentation,
  WithObjectProperties,
  Comparable<AvroSchemaField> {

  constructor(field: Schema.Field) : this(field = field, schema = AvroSchema(field.schema()))

  override val hashCode: AvroHashCode = AvroHashCode(field.hashCode())

  override val name: Name = Name.of(field)
  val type: SchemaType get() = schema.type

  val position: Int = field.pos()
  val order: Schema.Field.Order = field.order()

  val defaultValue: Any? = field.defaultVal()
  fun hasDefaultValue(): Boolean = defaultValue != null

  val aliases: Set<String> = field.aliases() ?: emptySet()

  override val documentation: Documentation? = Documentation.of(field)

  override val properties: ObjectProperties = ObjectProperties.ofNullable(field)
  val isNullable = schema.isNullable

  override fun compareTo(other: AvroSchemaField): Int = position.compareTo(other.position)
  override fun get(): Schema = schema.get()

  override fun toString() = field.toString()
  override fun equals(other: Any?): Boolean = field.hashCode() == other?.hashCode()
  override fun hashCode(): Int = field.hashCode()

}
