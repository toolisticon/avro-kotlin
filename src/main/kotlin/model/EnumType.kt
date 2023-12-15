package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.nullableToString
import io.toolisticon.avro.kotlin.value.*


@JvmInline
value class EnumType(override val schema: AvroSchema) : AvroNamedType,
  SchemaSupplier by schema,
  WithObjectProperties by schema
{

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

  override fun toString() = "EnumType(" +
    namespace.nullableToString("namespace='", suffix = "'") +
    documentation.nullableToString(", documentation=") +
    ", symbols=${symbols}" +
    defaultSymbol.nullableToString(", defaultSymbol='", suffix = "'") +
    ")"
}
