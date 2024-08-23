@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy.internal

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.logical.AvroKotlinLogicalTypeDefinition
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinConstructorPropertySpec
import io.toolisticon.kotlin.generation.spi.processor.executeSingle
import io.toolisticon.kotlin.generation.spi.strategy.KotlinCodeGenerationStrategyBase

/**
 * Internal strategy to create constructor properties ... this will not be configurable via SPI.
 * Might change later.
 *
 * TODO: code-generation lib will provide a constructor property strategy soon, replace parent
 */
internal class KotlinConstructorPropertyStrategy : KotlinCodeGenerationStrategyBase<SchemaDeclarationContext, RecordField, KotlinConstructorPropertySpec>(
  contextType = SchemaDeclarationContext::class, inputType = RecordField::class, specType = KotlinConstructorPropertySpec::class
) {

  override fun invoke(context: SchemaDeclarationContext, input: RecordField): KotlinConstructorPropertySpec {
    requireNotNull(input)
    val typeName = context[input.hashCode].suffixedTypeName
    val propertyBuilder = KotlinConstructorPropertySpecBuilder.builder(name = input.name.value, typeName).apply {
      input.documentation?.value?.let { addKdoc(it) }
    }
    context.processors(AvroKotlinLogicalTypeDefinition::class).executeSingle(context, input, propertyBuilder)

    return propertyBuilder.build()
  }

}
