package io.toolisticon.kotlin.avro.logical

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.PrimitiveSchemaTypeForLogicalType
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema

/**
 * A [AvroLogicalType] just defines the name and the [SchemaType] it is applicable
 * for. A simple logical type must _never_ require additional property parameters (decimal
 * with scale and precision is _not_ a simple logical type).
 *
 * We use the opinionated assumption, that logical types are only defined for [PrimitiveSchemaTypeForLogicalType]
 * as we do not see a relevant use-case for [LogicalType]s on records, arrays, maps and unions.
 *
 * An [AvroLogicalType] is best implemented as (data) Object, as it is de facto a singleton.
 *
 * @param <JVM_TYPE> how this type is represented in the jvm
 */
sealed class AvroLogicalType<JVM_TYPE : Any>(
  val name: LogicalTypeName,
  val type: PrimitiveSchemaTypeForLogicalType<JVM_TYPE>
) : LogicalType(name.value) {

  override fun toString() = toString(this::class.java.simpleName) {
    add(property = "name", value = name, wrap = "'")
    add("type", type)
  }

  fun schema() = AvroSchema(addToSchema(type.schema().get()))

  override fun addToSchema(schema: Schema): Schema {
    return super.addToSchema(schema)
  }

  override fun getName(): String = name.value

  override fun validate(schema: Schema) {
    super.validate(schema)
    require(SchemaType.valueOfType(schema.type) == type) { "$this is only valid for $type." }
  }
}
