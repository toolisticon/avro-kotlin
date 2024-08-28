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
import io.toolisticon.kotlin.avro.model.AvroTypesMap
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.model.wrapper.AvroSource
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.generation.spi.context.KotlinCodeGenerationContextBase

/**
 * Concrete implementation of [AvroDeclarationContext] for a [SchemaDeclaration].
 */
data class SchemaDeclarationContext(
  override val source: AvroSource,
  override val registry: AvroCodeGenerationSpiRegistry,
  override val properties: AvroKotlinGeneratorProperties,
  override val rootClassName: ClassName,
  override val canonicalName: CanonicalName,
  override val isRoot: Boolean,
  override val avroPoetTypes: AvroPoetTypes,
  override val avroTypes: AvroTypesMap,
  val schema: AvroSchema,
) : AvroDeclarationContext, KotlinCodeGenerationContextBase<SchemaDeclarationContext>(registry) {
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
        source = declaration.source,
        registry = registry,
        properties = properties,
        rootClassName = rootClassName,
        isRoot = true,
        avroPoetTypes = avroPoetTypes,
        canonicalName = declaration.canonicalName,
        avroTypes = declaration.avroTypes,
        schema = declaration.schema
      )
    }
  }

  override val contextType = SchemaDeclarationContext::class

  fun copyNonRoot() = copy(isRoot = false)

  val recordType by lazy {
    RecordType(schema)
  }

  val dataClassStrategies by lazy {
    registry.strategies.filter(AvroRecordTypeSpecStrategy::class)
  }

  val enumClassStrategies by lazy {
    registry.strategies.filter(AvroEnumTypeSpecStrategy::class)
  }
}
