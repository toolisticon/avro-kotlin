package io.toolisticon.avro.kotlin.model.support

import _ktx.StringKtx
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.ValueType
import org.apache.avro.Schema

/**
 * To support schemas of protocol message requests, we need to conserve the name (which is generated for message requests) when
 * creating the schema catalog.
 */
@JvmInline
internal value class NamedSchema private constructor(override val value: Pair<Name, Schema>) : ValueType<Pair<Name, Schema>> {
  companion object {
    val AvroSchema.namedSchema: NamedSchema get() = NamedSchema(this)
    val Schema.namedSchema: NamedSchema get() = NamedSchema(this)
    val NamedSchema.avroSchema get() = AvroSchema(name = this.name, schema = this.schema)
  }

  constructor(name: Name, schema: Schema) : this(name to schema)
  constructor(schema: AvroSchema) : this(name = schema.name, schema = schema.get())
  constructor(schema: Schema) : this(name = Name(schema), schema = schema)

  val name: Name get() = value.first

  val schema: Schema get() = value.second
  override fun toString(): String = StringKtx.toString("NamedSchema") {
    add(property = "name", value = name, wrap = "'")
    add(property = "hashCode", value = AvroHashCode(schema))
  }

}
