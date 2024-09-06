package io.toolisticon.kotlin.avro.builder

import io.toolisticon.kotlin.avro.model.NullType
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema.Companion.copy
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.avro.value.Documentation
import io.toolisticon.kotlin.avro.value.ObjectProperties
import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder.FieldAssembler

/**
 * Utilities to create [AvroSchema] or [AvroProtocol] from scratch without parsing [JsonString].
 */
object AvroBuilder {
  // this has to be lazy because [SchemaType#STRING] might not be initialized.
  val SCHEMA_UUID by lazy {
    primitiveSchema(SchemaType.STRING, LogicalTypes.uuid())
  }

  fun optional(schema: AvroSchema): AvroSchema = union(NullType.schema, schema)

  fun FieldAssembler<Schema>.uuid(fieldName: String, optional: Boolean = false): FieldAssembler<Schema> {
    val schema = if (optional) optional(SCHEMA_UUID) else SCHEMA_UUID

    return with(this.name(fieldName).type(schema.get())) {
      if (optional) {
        withDefault(null)
      } else {
        noDefault()
      }
    }
  }


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
      }
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

  fun AvroSchema.withLogicalType(logicalType: LogicalType) = copy(schema = logicalType.addToSchema(get()))

}
