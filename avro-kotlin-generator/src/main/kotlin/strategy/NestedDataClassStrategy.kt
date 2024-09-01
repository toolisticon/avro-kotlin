package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.api.AvroPoetType
import io.toolisticon.kotlin.avro.generator.poet.SerialNameAnnotation
import io.toolisticon.kotlin.avro.generator.poet.SerializableAnnotation
import io.toolisticon.kotlin.avro.generator.processor.KotlinDataClassFromRecordTypeProcessorBase
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.dataClassBuilder
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spi.processor.executeAll

/**
 * Generates a data class that does not require to be top-level/single
 * in a file. Used for inner types of schema and protocols.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class NestedDataClassStrategy : AvroRecordTypeSpecStrategy() {

  override fun invoke(context: SchemaDeclarationContext, input: RecordType): KotlinDataClassSpec {
    require(!context.isRoot) { "subTypes are non-root by definition." }

    // TODO: builder API auf TypeName erweitern
    val poetType: AvroPoetType = context[input.hashCode]

    val nestedDataClassBuilder = dataClassBuilder(poetType.suffixedTypeName as ClassName).apply {
      addKDoc(input.documentation)
      addAnnotation(SerializableAnnotation())

      if (poetType.isSuffixed) {
        addAnnotation(SerialNameAnnotation(input.canonicalName.fqn))
      }
    }

    context.registry.processors.filter(KotlinDataClassFromRecordTypeProcessorBase::class).executeAll(context, input, nestedDataClassBuilder)

    // adds RecordFields as constructor properties.
    parameterSpecs(context, input).forEach(nestedDataClassBuilder::addConstructorProperty)
//    ctx.processors.typeSpecProcessors(ctx, recordType, className, dataClassBuilder)
//
//    context.dataClassProcessors().invoke(context, input, dataClassBuilder)

    //context.processors(AbstractDataClassFromRecordTypeProcessor::class).executeAll(context, input, rootDataClassBuilder)

    return nestedDataClassBuilder.build()
  }

  override fun test(ctx: SchemaDeclarationContext, input: Any): Boolean = super.test(ctx, input) && !ctx.isRoot
}
