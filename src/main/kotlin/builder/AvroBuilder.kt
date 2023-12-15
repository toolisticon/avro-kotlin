package io.toolisticon.avro.kotlin.builder

import io.toolisticon.avro.kotlin.model.AvroProtocol
import io.toolisticon.avro.kotlin.model.AvroSchema
import io.toolisticon.avro.kotlin.value.ObjectProperties
import org.apache.avro.JsonProperties
import org.apache.avro.LogicalType
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.util.LinkedHashMap

object AvroBuilder {

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
    type: Schema.Type,
    logicalType: LogicalType = LOGICAL_TYPE_EMPTY,
    properties: ObjectProperties = ObjectProperties.EMPTY
  ): AvroSchema = AvroSchema(
    schema = Schema.create(type).apply {
      logicalType.addToSchema(this)
      properties.forEach { (k, v) -> this.addProp(k, v) }
    }, isRoot = false
  )

  fun union(vararg schemas:AvroSchema): AvroSchema {
    val s = schemas.map { it.schema }.toTypedArray()
    return AvroSchema(Schema.createUnion(*s))
  }

  fun array(schema: AvroSchema): AvroSchema {
    return AvroSchema(Schema.createArray(schema.schema))
  }

  fun map(schema: AvroSchema) : AvroSchema {
    return AvroSchema(Schema.createMap(schema.schema))
  }

  // ------ PROTOCOL Snippets
  /**
   * Constructs a similar Protocol instance with the same `name`,
   * `doc`, and `namespace` as {code p} has. It also copies all the
   * `props`.
   */
//  fun Protocol(p: Protocol) {
//    this(p.name, p.doc, p.namespace)
//    putAll(p)
//  }

//  private fun setName(name: String, namespace: String?) {
//    val lastDot = name.lastIndexOf('.')
//    if (lastDot < 0) {
//      this.name = name
//      this.namespace = namespace
//    } else {
//      this.name = name.substring(lastDot + 1)
//      this.namespace = name.substring(0, lastDot)
//    }
//    if (this.namespace != null && this.namespace!!.isEmpty()) {
//      this.namespace = null
//    }
//    types.space(this.namespace)
//  }

//
//  /** Create a one-way message.  */
//  @Deprecated("")
//  fun createMessage(name: String?, doc: String?, request: Schema?): AvroProtocol.Message {
//    return AvroProtocol.Message(name!!, doc, emptyMap<String, Any>(), request!!)
//  }
//  /** Set the types of this protocol.  */
//  fun setTypes(newTypes: Collection<Schema?>) {
//    types = Schema.Names()
//    for (s in newTypes) types.add(s)
//  }
//
//  /**
//   * Create a one-way message using the `name`, `doc`, and
//   * `props` of `m`.
//   */
//  fun createMessage(m: AvroProtocol.Message, request: Schema): AvroProtocol.Message {
//    return AvroProtocol.Message(m.name, m.doc, m, request)
//  }
//
//  /** Create a one-way message.  */
//  fun <T> createMessage(name: String, doc: String?, propMap: JsonProperties?, request: Schema): AvroProtocol.Message {
//    return AvroProtocol.Message(name, doc, propMap, request)
//  }
//
//  /** Create a one-way message.  */
//  fun <T> createMessage(name: String, doc: String?, propMap: Map<String, *>, request: Schema): AvroProtocol.Message {
//    return AvroProtocol.Message(name, doc, propMap, request)
//  }
//
//  /** Create a two-way message.  */
//  @Deprecated("")
//  fun createMessage(name: String, doc: String?, request: Schema, response: Schema, errors: Schema): AvroProtocol.Message {
//    return AvroProtocol.TwoWayMessage(name, doc, LinkedHashMap<String, String>(), request, response, errors)
//  }
//
//  /**
//   * Create a two-way message using the `name`, `doc`, and
//   * `props` of `m`.
//   */
//  fun createMessage(m: AvroProtocol.Message, request: Schema, response: Schema, errors: Schema): AvroProtocol.Message {
//    return AvroProtocol.TwoWayMessage(m.name, m.doc, m, request, response, errors)
//  }
//
//  /** Create a two-way message.  */
//  fun <T> createMessage(
//    name: String, doc: String?, propMap: JsonProperties, request: Schema, response: Schema,
//    errors: Schema
//  ): AvroProtocol.Message {
//    return AvroProtocol.TwoWayMessage(name, doc, propMap, request, response, errors)
//  }
//
//  /** Create a two-way message.  */
//  fun <T> createMessage(
//    name: String, doc: String?, propMap: Map<String, *>, request: Schema, response: Schema,
//    errors: Schema
//  ): AvroProtocol.Message {
//    return AvroProtocol.TwoWayMessage(name, doc, propMap, request, response, errors)
//  }

}
