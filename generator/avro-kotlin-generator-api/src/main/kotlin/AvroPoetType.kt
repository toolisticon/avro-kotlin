package io.toolisticon.kotlin.avro.generator.api

import _ktx.StringKtx
import com.squareup.kotlinpoet.TypeName
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.model.AvroType
import io.toolisticon.kotlin.avro.value.AvroHashCode

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

interface AvroPoetTypes : Iterable<AvroPoetType> {

  operator fun get(hashCode: AvroHashCode): AvroPoetType

}
