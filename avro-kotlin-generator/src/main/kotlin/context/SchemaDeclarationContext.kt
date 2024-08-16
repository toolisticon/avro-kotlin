package io.toolisticon.kotlin.avro.generator.context

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.generator.poet.AvroPoetTypeMap
import io.toolisticon.kotlin.avro.generator.rootClassName
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.generation.context.AbstractKotlinCodeGenerationContext
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry

/**
 * Concrete implementation of [AvroDeclarationContext] for a [SchemaDeclaration].
 */
class SchemaDeclarationContext(
  registry: AvroCodeGenerationSpiRegistry,
  override val properties: AvroKotlinGeneratorProperties,
  override val rootClassName: ClassName,
  override val isRoot: Boolean,
  override val avroPoetTypes: AvroPoetTypes,
  override val declaration: SchemaDeclaration,
) : AvroDeclarationContext<SchemaDeclaration>, AbstractKotlinCodeGenerationContext<SchemaDeclarationContext>(registry) {
  companion object {

    fun of(declaration: SchemaDeclaration, registry: AvroCodeGenerationSpiRegistry, properties: AvroKotlinGeneratorProperties = AvroKotlinGeneratorProperties()): SchemaDeclarationContext {
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

}
