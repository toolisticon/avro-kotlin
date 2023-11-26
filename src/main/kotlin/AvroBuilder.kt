package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.model.AvroSchema
import io.toolisticon.avro.kotlin.value.ObjectProperties
import org.apache.avro.LogicalType
import org.apache.avro.Schema

object AvroBuilder {

  /**
   * Create a wrapped primitive Schema based on type.
   *
   * @param type the schema type
   * @param logicalType optional logical type
   * @param objectProperties optional additional properties
   * @return wrapped primitive schema
   */
  fun primitiveSchema(
    type: Schema.Type,
    logicalType: LogicalType = AvroKotlin.LOGICALTYPE_EMPTY,
    properties: ObjectProperties = ObjectProperties.EMPTY
  ): AvroSchema = AvroSchema(
    schema = Schema.create(type).apply {
      logicalType.addToSchema(this)
      properties.forEach { (k, v) -> this.addProp(k, v) }
    }, isRoot = false
  )
}
