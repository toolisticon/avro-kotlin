package io.toolisticon.kotlin.avro.generator.strategy

import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec

class DefaultEnumClassStrategy : AbstractEnumClassFromEnumTypeStrategy() {
  override fun invoke(
    context: SchemaDeclarationContext,
    input: EnumType
  ): KotlinDataClassSpec {
    TODO("Not yet implemented")
  }
}
