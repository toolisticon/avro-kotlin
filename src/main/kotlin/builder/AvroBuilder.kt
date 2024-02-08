package io.toolisticon.avro.kotlin.builder

import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.Documentation
import io.toolisticon.avro.kotlin.value.ObjectProperties
import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder.FieldAssembler

/**
 * Utilities to create [AvroSchema] or [AvroProtocol] from scratch without parsing [JsonString].
 */
object AvroBuilder {

  fun FieldAssembler<Schema>.optionalUuid(fieldName: String) = this.name(fieldName)
    .type(
      Schema.createUnion(
        Schema.create(Schema.Type.NULL),
        primitiveSchema(SchemaType.STRING, LogicalTypes.uuid()).get()
      )
    )
    .withDefault(null)


  /**
   * Empty logical type that adds nothing to the schema.
   */
  val LOGICAL_TYPE_EMPTY = object : LogicalType("") {
    override fun addToSchema(schema: Schema): Schema = schema
    override fun validate(schema: Schema) {}
  }

  /**
   * Create a wrapped primitive Schema based on type.
   *
   * @param type the schema type
   * @param logicalType optional logical type
   * @param objectProperties optional additional properties
   * @return wrapped primitive schema
   */
  fun primitiveSchema(
    type: SchemaType,
    logicalType: LogicalType = LOGICAL_TYPE_EMPTY,
    properties: ObjectProperties = ObjectProperties.EMPTY
  ): AvroSchema {
    require(type.isPrimitive) { "Needs to be a valid primitive type: $type" }

    return AvroSchema(
      schema = Schema.create(type.get()).apply {
        logicalType.addToSchema(this)
        properties.forEach { (k, v) -> this.addProp(k, v) }
      }, isRoot = false
    )
  }

  fun union(vararg schemas: AvroSchema): AvroSchema {
    val s = schemas.map { it.get() }.toTypedArray()
    return AvroSchema(Schema.createUnion(*s))
  }

  fun array(schema: AvroSchema): AvroSchema {
    return AvroSchema(Schema.createArray(schema.get()))
  }

  fun map(schema: AvroSchema): AvroSchema {
    return AvroSchema(Schema.createMap(schema.get()))
  }

  fun fixed(name: CanonicalName, documentation: Documentation? = null, size: Int, logicalType: LogicalType = LOGICAL_TYPE_EMPTY): AvroSchema {
    val schema = Schema.createFixed(name.name.value, documentation?.value, name.namespace.value, size)
    logicalType.addToSchema(schema)

    return AvroSchema(schema)
  }

  fun AvroSchema.withLogicalType(logicalType: LogicalType) = AvroSchema(
    schema = logicalType.addToSchema(get()),
    name = name,
    isRoot = isRoot
  )

}
