package io.toolisticon.kotlin.avro.generator.api

import _ktx.StringKtx
import com.squareup.kotlinpoet.TypeName
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.model.AvroType
import io.toolisticon.kotlin.avro.value.AvroHashCode

/**
 * Wrapper that store the [AvroType] (schema type of avro property/record) together with the resolved
 * kotlin-poet [TypeName]. If [io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties.schemaTypeSuffix]
 * is configured, we additionally store the suffixed [TypeName] so we only have to deal with it once.
 */
data class AvroPoetType(
  val avroType: AvroType,
  val typeName: TypeName,
  val suffixedTypeName: TypeName = typeName,
) {

  val isSuffixed: Boolean = avroType is AvroNamedType && typeName != suffixedTypeName

  fun nullable(isNullable: Boolean = false) = copy(typeName = typeName.copy(nullable = isNullable))

  override fun toString() = StringKtx.toString("AvroPoetType") {
    add("key", avroType.schema.fingerprint)
    add("typeName", typeName)
    addIfNotNull("suffixedTypeName", if (isSuffixed) suffixedTypeName else null)
  }
}

/**
 * Allows iterating over a collection of [AvroPoetType]s and lookup via [AvroHashCode].
 */
interface AvroPoetTypes : Iterable<AvroPoetType> {

  /**
   * @param the hashcode of the type
   * @return the looked up type wrapper
   */
  operator fun get(hashCode: AvroHashCode): AvroPoetType

}
