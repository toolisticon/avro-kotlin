package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.github.oshai.kotlinlogging.KotlinLogging
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.asClassName
import io.toolisticon.kotlin.avro.generator.avroClassName
import io.toolisticon.kotlin.avro.generator.poet.SerialNameAnnotation
import io.toolisticon.kotlin.avro.generator.poet.SerializableAnnotation
import io.toolisticon.kotlin.avro.generator.processor.KotlinDataClassFromRecordTypeProcessorBase
import io.toolisticon.kotlin.avro.generator.processor.SchemaFileSpecProcessor
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.strategy.internal.KotlinConstructorPropertyStrategy
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.dataClassBuilder
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.fileBuilder
import io.toolisticon.kotlin.generation.spec.KotlinConstructorPropertySpecSupplier
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spi.EmptyInput
import io.toolisticon.kotlin.generation.spi.processor.executeAll
import io.toolisticon.kotlin.generation.spi.strategy.executeAll

/**
 * Generates a top-level data class for a given schema declaration.
 *
 * The generated class is the only top-level type in the file, all subtypes are generated
 * as inner classes, so everything declared in one schema stys in one file.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class SchemaTypesToFileStrategy : AvroFileSpecFromSchemaDeclarationStrategy() {
  private val logger = KotlinLogging.logger {}

  override fun invoke(context: SchemaDeclarationContext, input: SchemaDeclaration): KotlinFileSpec {
    val className = context.rootClassName ?: input.canonicalName.asClassName()

    val fileBuilder = fileBuilder(className = className)

    val rootDataClassBuilder = dataClassBuilder(className).apply {
      addKDoc(input.documentation)

      addAnnotation(SerializableAnnotation())

      if (context.properties.schemaTypeSuffix.isNotBlank()) {
        addAnnotation(SerialNameAnnotation(avroClassName(context.recordType)))
      }
      context.registry.processors(KotlinDataClassFromRecordTypeProcessorBase::class)
        .executeAll(context, input.recordType, this)
      // add constructor properties for RecordFields
      parameterSpecs(context, input.recordType).forEach(this::addConstructorProperty)
    }

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

    fileBuilder.addType(rootDataClassBuilder)
    typeSpecs.forEach(fileBuilder::addType)

    context.registry.processors(SchemaFileSpecProcessor::class).executeAll(context, EmptyInput, fileBuilder)
    return fileBuilder.build()
  }

  override fun test(context: SchemaDeclarationContext, input: Any): Boolean = super.test(context, input) && context.isRoot

  private fun parameterSpecs(context: SchemaDeclarationContext, input: RecordType): List<KotlinConstructorPropertySpecSupplier> {
    return input.fields.map { KotlinConstructorPropertyStrategy(context, it) }
  }
}
