package io.toolisticon.avro.kotlin.model

import _ktx.StringKtx.toString
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaChecks.isEnumType
import io.toolisticon.avro.kotlin.model.wrapper.SchemaSupplier
import io.toolisticon.avro.kotlin.value.*


@JvmInline
value class EnumType(override val schema: AvroSchema) :
  AvroNamedType,
  SchemaSupplier by schema,
  WithObjectProperties by schema {

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


  override fun toString() = toString("EnumType") {
    addIfNotNull(property = "namespace", value = namespace, wrap = "'")
    addIfNotNull(property = "documentation", value = documentation, wrap = "'")
    add(property = "symbols", symbols)
    addIfNotNull(property = "defaultSymbol", value = defaultSymbol, wrap = "'")
    addIfNotEmpty("properties", properties)
  }

}
