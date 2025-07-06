package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.firstUppercase
import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.AvroType.Companion.equalsFn
import io.toolisticon.kotlin.avro.model.AvroType.Companion.hashCodeFn
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema.Companion.copy
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isRecordType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaField
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.*
import org.apache.avro.Protocol.Message

/**
 * A protocol message defines a request and a response. By default,
 * the request is treated as a record with fields, where the fields are
 * representing parameters in the method signature.
 *
 * When we read a protocol, we create this abstraction.
 */
class RequestType(override val schema: AvroSchema) :
  AvroNamedTypeWithFields<RequestType, RequestField>,
  AvroMessageRequestType,
  WithEnclosedTypes,
  WithDocumentation,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  companion object {
    fun of(message: Message): RequestType {
      val name = Name("${message.name.firstUppercase()}Request")

      val schema = if (message.request.fields.isEmpty()) {
        EmptyType.schema.copy(name = name)
      } else {
        AvroSchema(schema = message.request, name = name)
      }.copy(isMessageRequest = true)

      return RequestType(schema)
    }
  }

  init {
    check(schema.isRecordType) { "Not a record type." }
    check(schema.isMessageRequest) { "Not a messageRequest." }
  }

  override val namespace: Namespace get() = schema.namespace
  val canonicalName: CanonicalName get() = schema.canonicalName

  override val fingerprint: AvroFingerprint = schema.fingerprint
  override val documentation: Documentation? = schema.documentation

  override val fields: List<RequestField> by lazy { schema.fields.map { RequestField(it, this) } }

  override val typesMap: AvroTypesMap by lazy { AvroTypesMap(fields.map { it.schema }) }

  val isRoot: Boolean get() = schema.isRoot
  val isEmpty: Boolean = fields.isEmpty()

  override fun toString() = toString("RequestType") {
    add("name", canonicalName)
    add("hashCode", hashCode)
    add("fingerprint", fingerprint)
    add("isEmpty", isEmpty)
    addIfNotNull("documentation", schema.documentation)
    addIfNotEmpty("properties", properties)
    addIfNotEmpty("fields", fields)
  }

  override fun equals(other: Any?) = equalsFn(other)
  override fun hashCode() = hashCodeFn()
}

/**
 * Used by [RequestType] to encapsulate [org.apache.avro.Schema.Field].
 */
class RequestField(schemaField: AvroSchemaField, memberOf: RequestType) : AvroFieldType<RequestType>(schemaField, memberOf)
