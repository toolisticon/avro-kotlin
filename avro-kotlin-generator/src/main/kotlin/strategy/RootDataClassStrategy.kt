package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.asClassName
import io.toolisticon.kotlin.avro.generator.avroClassName
import io.toolisticon.kotlin.avro.generator.poet.SerialNameAnnotation
import io.toolisticon.kotlin.avro.generator.poet.SerializableAnnotation
import io.toolisticon.kotlin.avro.generator.processor.KotlinDataClassFromRecordTypeProcessorBase
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.dataClassBuilder
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spi.processor.executeAll
import io.toolisticon.kotlin.generation.spi.strategy.executeAll
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation
import mu.KLogging

/**
 * Generates a top-level data class for a given schema declaration.
 *
 * The generated class is the only top-level type in the file, all subtypes are generated
 * as inner classes, so everything declared in one schema stys in one file.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class RootDataClassStrategy : AvroRecordTypeSpecStrategy() {
  companion object : KLogging()

  override fun invoke(context: SchemaDeclarationContext, input: RecordType): KotlinDataClassSpec {
    val className = context.rootClassName ?: input.canonicalName.asClassName()
    val rootDataClassBuilder = dataClassBuilder(className).apply {
      addKDoc(input.documentation)

      addAnnotation(
        GeneratedAnnotation(value = AvroKotlinGenerator.NAME)
          .date(context.properties.nowSupplier())
      )
      addAnnotation(SerializableAnnotation())

      if (context.properties.schemaTypeSuffix.isNotBlank()) {
        addAnnotation(SerialNameAnnotation(avroClassName(context.recordType)))
      }
    }

    context.registry.processors.filter(KotlinDataClassFromRecordTypeProcessorBase::class).executeAll(context, input, rootDataClassBuilder)

    // add constructor properties for RecordFields
    parameterSpecs(context, input).forEach(rootDataClassBuilder::addConstructorProperty)

    val nonRootCtx = context.copyNonRoot()

    val subTypesToGenerate = nonRootCtx.avroPoetTypes.filter {
      it.avroType is AvroNamedType
    }.distinctBy { it.avroType.hashCode }.map { it.avroType as AvroNamedType to it.typeName }


    val typeSpecs = subTypesToGenerate.flatMap { (type, _) ->
      when (type) {
        is RecordType -> nonRootCtx.dataClassStrategies.executeAll(nonRootCtx, type)
        is EnumType -> nonRootCtx.enumClassStrategies.executeAll(nonRootCtx, type)
        is ErrorType -> {
          logger.warn { "ignoring errorType: ${type.canonicalName}" }
          emptyList()
        }
        is FixedType, is RequestType -> {
          logger.debug { "ignoring ${type::class.simpleName} type: $type.canonicalName" }
          emptyList()
        }
      }
    }

    typeSpecs.forEach(rootDataClassBuilder::addType)
    context.generatedTypes[input.fingerprint] = className
    return rootDataClassBuilder.build()
  }

  override fun test(context: SchemaDeclarationContext, input: Any): Boolean = super.test(context, input) && context.isRoot
}