package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.ktx.ifTrue
import io.toolisticon.avro.kotlin.ktx.nullableToString
import io.toolisticon.avro.kotlin.value.*
import io.toolisticon.avro.kotlin.value.Documentation.Companion.shortenedIfPresent
import org.apache.avro.Schema

@JvmInline
value class RecordType(override val schema: AvroSchema) : AvroNamedType, WithDocumentation, SchemaSupplier by schema {

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
    val toStringName: String = ifTrue(
      isError,
      "ErrorType",
      ifTrue(
        isRoot,
        "RootRecordType",
        "RecordType"
      )
    )

    return "$toStringName(name='${name.withNamespace(namespace)}'" +
      ", hashCode='$hashCode', fingerprint='$fingerprint'" + documentation.shortenedIfPresent() +
      ", fields=$fields" +
      ")"
  }
}

@JvmInline
value class RecordField(val schemaField: AvroSchemaField) : AvroType, WithDocumentation, SchemaSupplier {
  override val schema: AvroSchema get() = schemaField.schema
  override val name: Name get() = schemaField.name
  override val hashCode: AvroHashCode get() = schemaField.schema.hashCode
  override val fingerprint: AvroFingerprint get() = schemaField.schema.fingerprint

  override fun get(): Schema = schemaField.schema.get()
  override val documentation: Documentation? get() = schemaField.documentation
}


@JvmInline
value class EnumType(override val schema: AvroSchema) : AvroNamedType, SchemaSupplier by schema {

  init {
    require(schema.isEnumType)
  }

  override val name: Name get() = schema.name
  override val namespace: Namespace? get() = schema.namespace
  override val hashCode: AvroHashCode get() = schema.hashCode
  override val fingerprint: AvroFingerprint get() = schema.fingerprint
  override val documentation: Documentation? get() = schema.documentation

  val symbols: List<String> get() = schema.enumSymbols
  val defaultSymbol: String? get() = get().enumDefault

  fun hasEnumSymbol(symbol: String) = symbols.contains(symbol)
  fun enumOrdinal(symbol: String) = symbols.indexOf(symbol)

  override fun toString() = schema.name.toString().removeSuffix(")") +
    namespace.nullableToString("namespace='", suffix = "'") +
    documentation.nullableToString(", documentation=") +
    ", symbols=${symbols}" +
    defaultSymbol.nullableToString(", defaultSymbol='", suffix = "'") +
    ")"
}
