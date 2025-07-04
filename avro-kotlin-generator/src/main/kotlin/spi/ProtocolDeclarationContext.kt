package io.toolisticon.kotlin.avro.generator.spi

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.TypeName
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.DefaultAvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.generator.poet.AvroPoetTypeMap
import io.toolisticon.kotlin.avro.generator.rootClassName
import io.toolisticon.kotlin.avro.model.AvroTypesMap
import io.toolisticon.kotlin.avro.model.EmptyType
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.avro.model.wrapper.AvroSource
import io.toolisticon.kotlin.avro.value.AvroFingerprint
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.generation.spi.context.KotlinCodeGenerationContextBase
import kotlin.reflect.KClass

/**
 * Concrete implementation of [AvroDeclarationContext] for a [ProtocolDeclaration].
 */
@OptIn(ExperimentalKotlinPoetApi::class)
data class ProtocolDeclarationContext(
  override val source: AvroSource,
  override val registry: AvroCodeGenerationSpiRegistry,
  override val properties: AvroKotlinGeneratorProperties,
  override val rootClassName: ClassName?,
  override val canonicalName: CanonicalName,
  override val isRoot: Boolean,
  override val avroPoetTypes: AvroPoetTypes,
  override val avroTypes: AvroTypesMap,
  val protocol: AvroProtocol,
  val tags: MutableMap<KClass<*>, Any?> = mutableMapOf(),
) : AvroDeclarationContext, KotlinCodeGenerationContextBase<ProtocolDeclarationContext>(registry) {
  companion object {

    internal fun ProtocolDeclarationContext.toSchemaDeclarationContext() = SchemaDeclarationContext(
      source = this.source,
      registry = this.registry,
      properties = this.properties,
      rootClassName = this.rootClassName,
      canonicalName = this.canonicalName,
      isRoot = false,
      avroPoetTypes = this.avroPoetTypes,
      avroTypes = this.avroTypes,
      schema = EmptyType.schema
    )

    fun of(
      declaration: ProtocolDeclaration,
      registry: AvroCodeGenerationSpiRegistry,
      properties: AvroKotlinGeneratorProperties = DefaultAvroKotlinGeneratorProperties(),
    ): ProtocolDeclarationContext {
      val rootClassName: ClassName = rootClassName(declaration, properties = properties)

      val avroPoetTypes: AvroPoetTypeMap = AvroPoetTypeMap.avroPoetTypeMap(
        rootClassName = null, // FIXME: root/nested behaviour must be redesigned/configurable
        properties = properties,
        avroTypes = declaration.avroTypes,
        logicalTypeMap = registry.logicalTypes,
      )

      return ProtocolDeclarationContext(
        source = declaration.source,
        registry = registry,
        properties = properties,
        rootClassName = rootClassName,
        canonicalName = declaration.canonicalName,
        isRoot = true,
        avroPoetTypes = avroPoetTypes,
        avroTypes = declaration.avroTypes,
        protocol = declaration.protocol
      )
    }
  }

  override val contextType = ProtocolDeclarationContext::class

  override val generatedTypes: MutableMap<AvroFingerprint, TypeName> = mutableMapOf()

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> tag(type: KClass<T>): T? = tags[type] as? T
}
