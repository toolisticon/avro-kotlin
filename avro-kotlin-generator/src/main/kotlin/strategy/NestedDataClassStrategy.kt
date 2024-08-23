@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.poet.SerializableAnnotation
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.dataClassBuilder
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec

/**
 * Generates a data class that does not require to be top-level/single
 * in a file. Used for inner types of schema and protocols.
 */
class NestedDataClassStrategy : AvroRecordTypeSpecStrategy() {

  override fun invoke(context: SchemaDeclarationContext, input: RecordType): KotlinDataClassSpec {
    require(!context.isRoot) { "subTypes are non-root by definition." }
    val className = context.className(input.hashCode)

    val rootDataClassBuilder = dataClassBuilder(className).apply {
      input.documentation?.value?.let(this::addKdoc)
      addAnnotation(SerializableAnnotation())
    }

    // adds RecordFields as constructor properties.
    parameterSpecs(context, input).forEach(rootDataClassBuilder::addConstructorProperty)
//    ctx.processors.typeSpecProcessors(ctx, recordType, className, dataClassBuilder)
//
//    context.dataClassProcessors().invoke(context, input, dataClassBuilder)

    //context.processors(AbstractDataClassFromRecordTypeProcessor::class).executeAll(context, input, rootDataClassBuilder)

    return rootDataClassBuilder.build()
  }

  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean = super.test(ctx, input) && !ctx.isRoot
}
