package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.model.AvroType.Companion.equalsFn
import io.toolisticon.avro.kotlin.model.AvroType.Companion.hashCodeFn
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.*
import io.toolisticon.avro.kotlin.value.Documentation.Companion.shortenedIfPresent

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
  val canonicalName : CanonicalName  get() = schema.canonicalName

  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val documentation: Documentation? get() = schema.documentation

  val isRoot: Boolean get() = schema.isRoot
  val fields: List<RecordField> by lazy { schema.fields.map { RecordField(it) } }

  override val typesMap: AvroTypesMap by lazy { AvroTypesMap(fields.map { it.schema }) }

  override fun toString(): String {
    val toStringName: String = AvroKotlin.StringKtx.ifTrue(
      isRoot,
      "RootRecordType",
      "RecordType"
    )

    return "$toStringName(name='${if (namespace == null) name else name + namespace!!}'" +
      ", hashCode='$hashCode', fingerprint='$fingerprint'" + documentation.shortenedIfPresent() +
      ", fields=$fields" +
      ")"
  }

  override fun equals(other: Any?) = equalsFn(other)
  override fun hashCode() = hashCodeFn()
}
