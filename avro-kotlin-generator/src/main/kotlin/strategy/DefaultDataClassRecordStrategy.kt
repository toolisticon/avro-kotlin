package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinDataClassSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spi.strategy.DataClassSpecStrategy

@OptIn(ExperimentalKotlinPoetApi::class)
class DefaultDataClassRecordStrategy : DataClassSpecStrategy<SchemaDeclarationContext, RecordType>(
  contextType = SchemaDeclarationContext::class, inputType = RecordType::class
) {
  override fun invoke(context: SchemaDeclarationContext, input: RecordType): KotlinDataClassSpec {
    val className: ClassName = if (context.isRoot) context.rootClassName else context.className(input.hashCode)

    val dataClassBuilder = KotlinDataClassSpecBuilder.builder(className)

    val parameterSpecs = input.fields.map { field ->
      val typeName = context[field.hashCode].suffixedTypeName

      KotlinConstructorPropertySpecBuilder.builder(name = field.name.value, typeName).apply {
        // context.processors.dataClassParameterSpecProcessors(context, field, this)
      }
    }
//
//    if (context.isRoot) {
//      generateSubTypes(context.nonRoot(), dataClassBuilder)
//    }
//
//    ctx.processors.typeSpecProcessors(ctx, recordType, className, dataClassBuilder)

    parameterSpecs.forEach(dataClassBuilder::addConstructorProperty)

    return dataClassBuilder.build()
  }
}
