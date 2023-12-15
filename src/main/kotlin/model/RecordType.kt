package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.value.*
import io.toolisticon.avro.kotlin.value.Documentation.Companion.shortenedIfPresent

@JvmInline
value class RecordType(override val schema: AvroSchema) : AvroNamedType, WithDocumentation, SchemaSupplier by schema, WithObjectProperties by schema {

  init {
    check(schema.isRecordType) { "not a record type." }
    check(fields.isNotEmpty()) { "a record needs at least one field." }
  }

  override val name: Name get() = schema.name
  override val namespace: Namespace? get() = schema.namespace
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val documentation: Documentation? get() = schema.documentation

  val isError: Boolean get() = schema.isError
  val isRoot: Boolean get() = schema.isRoot
  val fields: List<RecordField> get() = schema.fields.map { RecordField(it) }

  override fun toString(): String {
    val toStringName: String = AvroKotlin.StringKtx.ifTrue(
      isError,
      "ErrorType",
      AvroKotlin.StringKtx.ifTrue(
        isRoot,
        "RootRecordType",
        "RecordType"
      )
    )

    return "$toStringName(name='${if (namespace == null) name else name + namespace!!}'" +
      ", hashCode='$hashCode', fingerprint='$fingerprint'" + documentation.shortenedIfPresent() +
      ", fields=$fields" +
      ")"
  }
}
