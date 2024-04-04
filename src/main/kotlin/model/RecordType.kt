package io.toolisticon.avro.kotlin.model

import _ktx.StringKtx
import _ktx.StringKtx.toString
import io.toolisticon.avro.kotlin.model.AvroType.Companion.equalsFn
import io.toolisticon.avro.kotlin.model.AvroType.Companion.hashCodeFn
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaChecks.isRecordType
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.*


/**
 * Records use the type name “record” and support the following attributes:
 *
 * * name: a JSON string providing the name of the record (required).
 * * namespace, a JSON string that qualifies the name (optional);
 * * doc: a JSON string providing documentation to the user of this schema (optional).
 * * aliases: a JSON array of strings, providing alternate names for this record (optional).
 * * fields: a JSON array, listing fields (required). Each field is a JSON object with the following attributes:
 *   * name: a JSON string providing the name of the field (required), and
 *   * doc: a JSON string describing this field for users (optional).
 *   * type: a schema, as defined above
 *   * default: A default value for this field, only used when reading instances that lack the field for schema evolution purposes. The presence of a default value does not make the field optional at encoding time. Permitted values depend on the field’s schema type, according to the table below. Default values for union fields correspond to the first schema in the union. Default values for bytes and fixed fields are JSON strings, where Unicode code points 0-255 are mapped to unsigned 8-bit byte values 0-255. Avro encodes a field even if its value is equal to its default.
 */
class RecordType(override val schema: AvroSchema) :
  AvroNamedType,
  WithEnclosedTypes,
  WithDocumentation,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

  init {
    check(schema.isRecordType) { "Not a record type." }
    check(schema.fields.isNotEmpty()) { "Record[$namespace.$name]: must have at least one field." }
  }

  override val namespace: Namespace get() = schema.namespace
  val canonicalName: CanonicalName get() = schema.canonicalName

  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val documentation: Documentation? get() = schema.documentation

  val isRoot: Boolean get() = schema.isRoot
  val fields: List<RecordField> by lazy { schema.fields.map { RecordField(it) } }

  fun getField(name: Name): RecordField? = fields.find { it.name == name }

  override val typesMap: AvroTypesMap by lazy { AvroTypesMap(fields.map { it.schema }) }

  override fun toString() = toString(
    StringKtx.ifTrue(
      isRoot,
      "RootRecordType",
      "RecordType"
    )
  ) {
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
