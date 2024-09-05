package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.AvroType.Companion.equalsFn
import io.toolisticon.kotlin.avro.model.AvroType.Companion.hashCodeFn
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol.Companion.requestName
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isRecordType
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.*
import org.apache.avro.Protocol

/**
 * A protocol message defines a request and a response. By default,
 * the request is treated as a record with fields, where the fields are
 * representing parameters in the method signature.
 *
 * When we read a protocol, we create this abstraction.
 */
class RequestType(override val schema: AvroSchema) : AvroNamedType, AvroMessageRequestType,
    WithEnclosedTypes,
    WithDocumentation,
  SchemaSupplier by schema,
  WithObjectProperties by schema {


  fun schemaForMessageRequest(message: Protocol.Message): AvroSchema = if (message.request.fields.isEmpty()) {
    EmptyType.schema
  } else {
    AvroSchema(schema = message.request, name = requestName(message))
  }


  init {
    check(schema.isRecordType) { "Not a record type." }
  }

  override val namespace: Namespace get() = schema.namespace
  val canonicalName: CanonicalName get() = schema.canonicalName

  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val documentation: Documentation? get() = schema.documentation

  val isRoot: Boolean get() = schema.isRoot
  val fields: List<RecordField> by lazy { schema.fields.map { RecordField(it) } }

  fun getField(name: Name): RecordField? = fields.find { it.name == name }

  override val typesMap: AvroTypesMap by lazy { AvroTypesMap(fields.map { it.schema }) }

  override fun toString() = toString("RequestType") {
    add("name", canonicalName)
    add("hashCode", hashCode)
    add("fingerprint", fingerprint)
    addIfNotNull("documentation", schema.documentation)
    addIfNotEmpty("properties", properties)
    addIfNotEmpty("fields", fields)
  }

  override fun equals(other: Any?) = equalsFn(other)
  override fun hashCode() = hashCodeFn()
}
