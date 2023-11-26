package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.AvroKotlin.documentation
import io.toolisticon.avro.kotlin.value.Documentation
import io.toolisticon.avro.kotlin.value.Name
import org.apache.avro.Schema

class AvroSchemaField(
  private val field: Schema.Field,
) : SchemaSupplier, WithDocumentation, Comparable<AvroSchemaField> {

  val name: Name = AvroKotlin.name(field)
  val schema: AvroSchema = AvroSchema(field.schema())

  val position: Int = field.pos()
  val order: Schema.Field.Order = field.order()

  val defaultValue: Any? = field.defaultVal()
  fun hasDefaultValue(): Boolean = defaultValue != null

  val aliases: Set<String> = field.aliases() ?: emptySet()

  override val documentation: Documentation? = documentation(field)

  val isNullable = schema.isNullable

  override fun compareTo(other: AvroSchemaField): Int = position.compareTo(other.position)
  override fun get(): Schema = schema.get()

  override fun toString() = field.toString()
  override fun equals(other: Any?): Boolean = field.hashCode() == other?.hashCode()
  override fun hashCode(): Int = field.hashCode()

}
