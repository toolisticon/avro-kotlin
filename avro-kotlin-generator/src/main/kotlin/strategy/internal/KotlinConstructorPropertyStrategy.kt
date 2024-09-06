package io.toolisticon.kotlin.avro.generator.strategy.internal

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.logical.AvroKotlinLogicalTypeDefinition
import io.toolisticon.kotlin.avro.generator.processor.ConstructorPropertyFromRecordFieldProcessorBase
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.spec.KotlinConstructorPropertySpec
import io.toolisticon.kotlin.generation.spi.processor.executeAll
import io.toolisticon.kotlin.generation.spi.processor.executeSingle
import io.toolisticon.kotlin.generation.spi.strategy.KotlinConstructorPropertySpecStrategy

/**
 * Create
 *
 */
@OptIn(ExperimentalKotlinPoetApi::class)
internal class KotlinConstructorPropertyStrategy : InternalStrategy, KotlinConstructorPropertySpecStrategy<SchemaDeclarationContext, RecordField>(
  contextType = SchemaDeclarationContext::class, inputType = RecordField::class
) {

  override fun invoke(context: SchemaDeclarationContext, input: RecordField): KotlinConstructorPropertySpec {
    val typeName = context[input.hashCode].suffixedTypeName
    val builder = KotlinCodeGeneration.builder.constructorPropertyBuilder(name = input.name.value, typeName).apply {
      addKDoc(input.documentation)
    }
    context.processors(AvroKotlinLogicalTypeDefinition::class).executeSingle(context, input, builder)
    context.processors(ConstructorPropertyFromRecordFieldProcessorBase::class).executeAll(context, input, builder)

    return builder.build()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }
}
