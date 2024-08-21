package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.processor.AbstractDataClassFromRecordTypeProcessor
import io.toolisticon.kotlin.avro.generator.processor.AbstractPropertyFromRecordFieldProcessor
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.dataClassBuilder
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spi.processor.executeAll

/**
 * Use [RecordType] to create a serializable data class.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class DefaultDataClassRecordStrategy : AbstractDataClassFromRecordTypeStrategy() {

  override fun invoke(context: SchemaDeclarationContext, input: RecordType): KotlinDataClassSpec {
    val className: ClassName = if (context.isRoot) context.rootClassName else context.className(input.hashCode)

    val dataClassBuilder = dataClassBuilder(className).apply {
      input.documentation?.let { addKdoc(it.value) }
    }

    val parameterSpecs = input.fields.map { field ->
      val typeName = context[field.hashCode].suffixedTypeName
      val propertyBuilder = KotlinConstructorPropertySpecBuilder.builder(name = field.name.value, typeName)
      context.processors(AbstractPropertyFromRecordFieldProcessor::class).executeAll(context, field, propertyBuilder)
    }
//
//    if (context.isRoot) {
//      generateSubTypes(context.nonRoot(), dataClassBuilder)
//    }
//
//    ctx.processors.typeSpecProcessors(ctx, recordType, className, dataClassBuilder)
//
//    context.dataClassProcessors().invoke(context, input, dataClassBuilder)

    context.processors(AbstractDataClassFromRecordTypeProcessor::class).executeAll(context, input, dataClassBuilder)
    parameterSpecs.forEach(dataClassBuilder::addConstructorProperty)

    return dataClassBuilder.build()
  }

  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean {
    return super.test(ctx, input) && input is RecordType
  }
}
