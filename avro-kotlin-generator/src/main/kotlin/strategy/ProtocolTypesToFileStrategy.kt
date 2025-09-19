package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.TypeName
import io.github.oshai.kotlinlogging.KotlinLogging
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.processor.ProtocolFileSpecProcessor
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext.Companion.toSchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.strategy.internal.KotlinErrorTypeStrategy
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.avro.value.toKebabCase
import io.toolisticon.kotlin.generation.FileName
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.fileBuilder
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.name.className
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spec.KotlinGeneratorTypeSpec
import io.toolisticon.kotlin.generation.spi.EmptyInput
import io.toolisticon.kotlin.generation.spi.processor.executeAll
import io.toolisticon.kotlin.generation.spi.strategy.executeAll

@OptIn(ExperimentalKotlinPoetApi::class)
class ProtocolTypesToFileStrategy(
  private val determineFileName: DetermineFileName,
  private val avroNamedTypeFilter: AvroNamedTypeFilter,
) : AvroFileSpecFromProtocolDeclarationStrategy() {
  companion object {

    /**
     * Gives the name of the generated file.
     */
    fun interface DetermineFileName : (ProtocolDeclarationContext, ProtocolDeclaration) -> FileName

    /**
     * Returns `true` if the type should be considered for generation.
     */
    fun interface AvroNamedTypeFilter : (AvroNamedType) -> Boolean

    val defaultDetermineFileName = DetermineFileName { context, input ->
      val protocol = input.protocol
      val kebabCase = protocol.name.toKebabCase()
      className(input.canonicalName.namespace.value, "$kebabCase-types")
    }

    val defaultAvroNamedTypeFilter = AvroNamedTypeFilter { true }
  }

  private val logger = KotlinLogging.logger {}

  /**
   * Default constructor used by spi.
   */
  @Suppress("unused")
  constructor() : this(determineFileName = defaultDetermineFileName, avroNamedTypeFilter = defaultAvroNamedTypeFilter)

  override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec {
    val fileBuilder = fileBuilder(className = determineFileName(context, input))

    val schemaDeclarationContext = context.toSchemaDeclarationContext()

    val subTypesToGenerate = schemaDeclarationContext.avroPoetTypes.filter {
      it.avroType is AvroNamedType
    }.distinctBy { it.avroType.hashCode }.map { it.avroType as AvroNamedType to it.typeName }

    val typeSpecs: List<KotlinGeneratorTypeSpec<*>> = subTypesToGenerate.filter { avroNamedTypeFilter(it.first) }
      .flatMap<Pair<AvroNamedType, TypeName>, KotlinGeneratorTypeSpec<*>> { (type, _) ->
        when (type) {
          is RecordType -> schemaDeclarationContext.dataClassStrategies.executeAll(schemaDeclarationContext, type)
          is EnumType -> schemaDeclarationContext.enumClassStrategies.executeAll(schemaDeclarationContext, type)
          is ErrorType -> listOf(KotlinErrorTypeStrategy.execute(schemaDeclarationContext, type)).filterNotNull()
          is FixedType, is RequestType -> {
            logger.debug { "ignoring ${type::class.simpleName} type: $type.canonicalName" }
            emptyList()
          }
        }
      }

    typeSpecs.forEach(fileBuilder::addType)

    context.registry.processors(ProtocolFileSpecProcessor::class).executeAll(context, EmptyInput, fileBuilder)
    return fileBuilder.build()
  }
}
