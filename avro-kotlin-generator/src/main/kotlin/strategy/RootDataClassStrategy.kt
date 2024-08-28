@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.avroClassName
import io.toolisticon.kotlin.avro.generator.poet.SerialNameAnnotation
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
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation

/**
 * Generates a top-level data class for a given schema declaration.
 *
 * The generated class is the only top-level type in the file, all subtypes are generated
 * as inner classes, so everything declared in one schema stys in one file.
 */
class RootDataClassStrategy : AvroRecordTypeSpecStrategy() {

  override fun invoke(context: SchemaDeclarationContext, input: RecordType): KotlinDataClassSpec {
    val rootDataClassBuilder = dataClassBuilder(context.rootClassName).apply {
      input.documentation?.value?.let(this::addKdoc)
      addAnnotation(
        GeneratedAnnotation(value = AvroKotlinGenerator.NAME)
          .date(context.properties.nowSupplier())
      )
      addAnnotation(SerializableAnnotation())

      if (context.properties.schemaTypeSuffix.isNotBlank()) {
        addAnnotation(SerialNameAnnotation(avroClassName(context.recordType)))
      }
    }

    // add constructor properties for RecordFields
    parameterSpecs(context, input).forEach(rootDataClassBuilder::addConstructorProperty)

    val nonRootCtx = context.copyNonRoot()

    val subTypesToGenerate = nonRootCtx.avroPoetTypes.filter {
      it.avroType is AvroNamedType
    }.map { it.avroType as AvroNamedType to it.typeName }


    val typeSpecs = subTypesToGenerate.flatMap { (type, _) ->
      when (type) {
        is RecordType -> nonRootCtx.dataClassStrategies.executeAll(nonRootCtx, type)
        is EnumType -> nonRootCtx.enumClassStrategies.executeAll(nonRootCtx, type)
        is ErrorType -> TODO("error is not implemented yet")
        is FixedType -> TODO("fixed is not implemented yet")
      }
    }

    typeSpecs.forEach(rootDataClassBuilder::addType)
    return rootDataClassBuilder.build()
  }

  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean = super.test(ctx, input) && ctx.isRoot
}
