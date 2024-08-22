@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.poet.GeneratedAnnotation
import io.toolisticon.kotlin.avro.generator.poet.SerializableAnnotation
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.avro.model.ErrorType
import io.toolisticon.kotlin.avro.model.FixedType
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.dataClassBuilder
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spi.strategy.executeAll
import io.toolisticon.kotlin.generation.spi.strategy.executeSingle


class RootDataClassStrategy : AvroRecordTypeSpecStrategy() {

  override fun invoke(context: SchemaDeclarationContext, input: RecordType): KotlinDataClassSpec {
    val rootDataClassBuilder = dataClassBuilder(context.rootClassName).apply {
      input.documentation?.value?.let(this::addKdoc)
      addAnnotation(GeneratedAnnotation(value = AvroKotlinGenerator.NAME).date(context.nowSupplier()))
      addAnnotation(SerializableAnnotation())
    }

    val parameterSpecs = parameterSpecs(context, input)

    val nonRootCtx = context.copyNonRoot()

    val subTypesToGenerate = nonRootCtx.avroPoetTypes.filter {
      it.avroType is AvroNamedType
    }.map { it.avroType as AvroNamedType to it.typeName }


    val typeSpecs = subTypesToGenerate.flatMap { (type, _) ->
      when (type) {
        is RecordType -> nonRootCtx.dataClassStrategies.executeAll(nonRootCtx, type)
        is EnumType -> nonRootCtx.enumClassStrategies.executeAll(nonRootCtx, type)
        is ErrorType -> TODO()
        is FixedType -> TODO()
      }
    }

    typeSpecs.forEach(rootDataClassBuilder::addType)

//
//    ctx.processors.typeSpecProcessors(ctx, recordType, className, dataClassBuilder)
//
//    context.dataClassProcessors().invoke(context, input, dataClassBuilder)

//    context.processors(
//      AbstractDataClassFromRecordTypeProcessor::
//      class
//    ).executeAll(context, input, rootDataClassBuilder)
    parameterSpecs.forEach(rootDataClassBuilder::addConstructorProperty)

    return rootDataClassBuilder.build()
  }

  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean = super.test(ctx, input) && ctx.isRoot
}
