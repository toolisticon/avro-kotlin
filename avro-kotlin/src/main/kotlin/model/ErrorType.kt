package io.toolisticon.kotlin.avro.model

import _ktx.StringKtx.toString
import io.toolisticon.kotlin.avro.model.AvroType.Companion.equalsFn
import io.toolisticon.kotlin.avro.model.AvroType.Companion.hashCodeFn
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isErrorType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaField
import io.toolisticon.kotlin.avro.model.wrapper.SchemaSupplier
import io.toolisticon.kotlin.avro.value.*


/**
 * Error is a special [RecordType] that is used to define exceptions on [org.apache.avro.Protocol].Messages.
 */
class ErrorType(override val schema: AvroSchema) :
  AvroNamedTypeWithFields<ErrorType, ErrorField>,
  WithEnclosedTypes,
  WithDocumentation,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    check(schema.isErrorType) { "Record[$canonicalName]: not an ErrorType." }
  }

  override val namespace: Namespace get() = schema.namespace
  val canonicalName: CanonicalName get() = namespace + name
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val documentation: Documentation? get() = schema.documentation

  val isRoot: Boolean get() = schema.isRoot
  override val fields: List<ErrorField> by lazy { schema.fields.map { ErrorField(it, this) } }

  override val typesMap: AvroTypesMap = AvroTypesMap(fields.map { it.schema })

  override fun toString() = toString("ErrorType") {
    add("namespace", namespace)
    add("name", name)
    add("hashCode", hashCode)
    add("fingerprint", fingerprint)
    addIfNotNull("documentation", documentation)
    add("fields", fields)
  }

  override fun equals(other: Any?) = equalsFn(other)
  override fun hashCode() = hashCodeFn()
}

/**
 * Used by [ErrorType] to encapsulate [org.apache.avro.Schema.Field].
 */
class ErrorField(schemaField: AvroSchemaField, memberOf: ErrorType) : AvroFieldType<ErrorType>(schemaField, memberOf)
