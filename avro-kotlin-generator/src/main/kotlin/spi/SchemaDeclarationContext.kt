@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.DefaultAvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.generator.poet.AvroPoetTypeMap
import io.toolisticon.kotlin.avro.generator.rootClassName
import io.toolisticon.kotlin.avro.generator.strategy.AvroEnumTypeSpecStrategy
import io.toolisticon.kotlin.avro.generator.strategy.AvroRecordTypeSpecStrategy
import io.toolisticon.kotlin.generation.spi.context.KotlinCodeGenerationContextBase

/**
 * Concrete implementation of [AvroDeclarationContext] for a [SchemaDeclaration].
 */
data class SchemaDeclarationContext(
  override val registry: AvroCodeGenerationSpiRegistry,
  override val properties: AvroKotlinGeneratorProperties,
  override val rootClassName: ClassName,
  override val isRoot: Boolean,
  override val avroPoetTypes: AvroPoetTypes,
  override val declaration: SchemaDeclaration
) : AvroDeclarationContext<SchemaDeclaration>, KotlinCodeGenerationContextBase<SchemaDeclarationContext>(registry) {
  companion object {

    fun of(
      declaration: SchemaDeclaration,
      registry: AvroCodeGenerationSpiRegistry,
      properties: AvroKotlinGeneratorProperties = DefaultAvroKotlinGeneratorProperties(),
    ): SchemaDeclarationContext {
      val rootClassName: ClassName = rootClassName(declaration, properties = properties)

      val avroPoetTypes: AvroPoetTypeMap = AvroPoetTypeMap.avroPoetTypeMap(
        rootClassName = rootClassName,
        properties = properties,
        avroTypes = declaration.avroTypes - declaration.recordType.hashCode,
        logicalTypeMap = registry.logicalTypes,
      )

      return SchemaDeclarationContext(
        registry = registry,
        properties = properties,
        rootClassName = rootClassName,
        isRoot = true,
        avroPoetTypes = avroPoetTypes,
        declaration = declaration,
      )
    }
  }

  override val contextType = SchemaDeclarationContext::class

  fun copyNonRoot() = copy(isRoot = false)

  val dataClassStrategies by lazy {
    registry.strategies.filter(AvroRecordTypeSpecStrategy::class)
  }

  val enumClassStrategies by lazy {
    registry.strategies.filter(AvroEnumTypeSpecStrategy::class)
  }
}
