package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.TypeName
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
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.generation.spi.context.KotlinCodeGenerationContextBase
import kotlin.reflect.KClass

/**
 * Concrete implementation of [AvroDeclarationContext] for a [SchemaDeclaration].
 */
@OptIn(ExperimentalKotlinPoetApi::class)
data class SchemaDeclarationContext(
  override val source: AvroSource,
  override val registry: AvroCodeGenerationSpiRegistry,
  override val properties: AvroKotlinGeneratorProperties,
  override val rootClassName: ClassName?,
  override val canonicalName: CanonicalName,
  override val isRoot: Boolean,
  override val avroPoetTypes: AvroPoetTypes,
  override val avroTypes: AvroTypesMap,
  val schema: AvroSchema,
  val tags: MutableMap<KClass<*>, Any?> = mutableMapOf(),
) : AvroDeclarationContext, KotlinCodeGenerationContextBase<SchemaDeclarationContext>(registry) {
  companion object {

    fun of(
      declaration: SchemaDeclaration,
      registry: AvroCodeGenerationSpiRegistry,
      properties: AvroKotlinGeneratorProperties = DefaultAvroKotlinGeneratorProperties(),
    ): SchemaDeclarationContext {
      val rootClassName: ClassName = rootClassName(declaration, properties = properties)

      val avroPoetTypes: AvroPoetTypeMap = AvroPoetTypeMap.avroPoetTypeMap(
        rootClassName = null, // FIXME: root/nested behaviour must be redesigned/configurable,
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

  override val generatedTypes: MutableMap<AvroFingerprint, TypeName> = mutableMapOf()

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> tag(type: KClass<T>): T? = tags[type] as? T
}
