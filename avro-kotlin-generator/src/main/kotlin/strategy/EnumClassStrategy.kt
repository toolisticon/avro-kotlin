package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationStrategy
import io.toolisticon.kotlin.generation.spi.strategy.AbstractKotlinCodeGenerationStrategy

@OptIn(ExperimentalKotlinPoetApi::class)
class EnumClassStrategy : AbstractKotlinCodeGenerationStrategy<SchemaDeclarationContext, EnumType, KotlinEnumClassSpec>(
  contextType = SchemaDeclarationContext::class, inputType = EnumType::class, specType = KotlinEnumClassSpec::class
) {
  override fun invoke(
    context: SchemaDeclarationContext,
    input: EnumType
  ): KotlinEnumClassSpec {
    TODO()
  }

  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean {
    return super.test(ctx, input) && input is EnumType
  }
}
