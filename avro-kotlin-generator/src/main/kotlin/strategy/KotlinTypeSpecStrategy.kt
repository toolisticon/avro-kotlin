@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.generation.spec.KotlinGeneratorTypeSpec
import io.toolisticon.kotlin.generation.spi.strategy.AbstractKotlinCodeGenerationStrategy
import kotlin.reflect.KClass

abstract class KotlinTypeSpecStrategy<INPUT : AvroNamedType, SPEC : KotlinGeneratorTypeSpec<SPEC>>(
  inputType: KClass<INPUT>,
  specType: KClass<SPEC>,
) : AbstractKotlinCodeGenerationStrategy<SchemaDeclarationContext, INPUT, SPEC>(
  contextType = SchemaDeclarationContext::class, inputType = inputType, specType = specType
) {
  override fun invoke(context: SchemaDeclarationContext, input: INPUT): SPEC {
    TODO("Not yet implemented")
  }
}
