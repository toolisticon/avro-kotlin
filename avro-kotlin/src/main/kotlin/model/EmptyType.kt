package io.toolisticon.avro.kotlin.model

import _ktx.StringKtx.toString
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.*
import org.apache.avro.Protocol
import org.apache.avro.Schema

/**
 * EmptyType is a special type of [io.toolisticon.avro.kotlin.model.SchemaType.RECORD] that has an empty [Name]
 * and no [io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaField]s.
 *
 * Its only use case is when it is used as an empty request array when defining a protocol message.
 * It is based on an invalid [Schema] and can not be created via parsing a string or building a schema.
 */
data object EmptyType : AvroType {
  /**
   * As this type does not conform to the RECORD statements of the specification (it has no name and no fields),
   * we con only create it by parsing a protocol and extract the request type.
   */
  private val SCHEMA = Protocol.parse(
    """{ "namespace": "", "protocol":"",
        "messages": {
          "empty": {
            "request": [],
            "response": "null",
            "one-way": true
          }
        }
    }
  """.trimIndent()
  ).messages["empty"]!!.request

  override val name: Name get() = Name.EMPTY
  override val schema: AvroSchema = AvroSchema(schema = SCHEMA, name = name)
  override val hashCode: AvroHashCode get() = schema.hashCode

  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val json: JsonString get() = schema.json
  override fun get(): Schema = schema.get()
  override val properties: ObjectProperties = ObjectProperties.EMPTY

  override fun toString(): String = toString("EmptyType") {
    add("hashCode", hashCode.hex)
    add("fingerprint", fingerprint.hex)
  }

}
