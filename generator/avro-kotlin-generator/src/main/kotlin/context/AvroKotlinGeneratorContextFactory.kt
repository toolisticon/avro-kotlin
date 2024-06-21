package io.toolisticon.kotlin.avro.generator.context

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.processor.AvroKotlinGeneratorProcessors
import io.toolisticon.kotlin.avro.generator.api.strategy.AvroKotlinGeneratorStrategies
import io.toolisticon.kotlin.avro.generator.poet.AvroPoetTypeMap
import io.toolisticon.kotlin.avro.generator.spi.AvroKotlinGeneratorServiceLoader

/**
 * Keeps all required data to create a ctx based on a declaration.
 */
class AvroKotlinGeneratorContextFactory(
  val properties: AvroKotlinGeneratorProperties,
  val processors: AvroKotlinGeneratorProcessors,
  val strategies: AvroKotlinGeneratorStrategies,
) {

  constructor(
    properties: AvroKotlinGeneratorProperties = AvroKotlinGeneratorProperties(),
    loader: AvroKotlinGeneratorServiceLoader = AvroKotlinGeneratorServiceLoader.load(),
  ) : this(properties, loader, loader)

  fun create(declaration: SchemaDeclaration): SchemaDeclarationContextData {
    val rootClassName: ClassName = AvroKotlinGeneratorApi.rootClassName(declaration, properties = properties)

    val avroPoetTypes: AvroPoetTypeMap = AvroPoetTypeMap.avroPoetTypeMap(
      rootClassName = rootClassName,
      properties = properties,
      avroTypes = declaration.avroTypes - declaration.recordType.hashCode,
      logicalTypeMap = processors.logicalTypes,
    )

    return SchemaDeclarationContextData(
      rootClassName = rootClassName,
      isRoot = true,
      properties = properties,
      declaration = declaration,
      avroPoetTypes = avroPoetTypes,
      processors = processors,
      strategies = strategies
    )
  }

  fun create(declaration: ProtocolDeclaration): ProtocolDeclarationContext {
    val rootClassName = AvroKotlinGeneratorApi.rootClassName(declaration, properties)

    val avroPoetTypes: AvroPoetTypeMap = AvroPoetTypeMap.avroPoetTypeMap(
      rootClassName = rootClassName,
      properties = properties,
      avroTypes = declaration.avroTypes,
       logicalTypeMap = processors.logicalTypes,
    )

    return ProtocolDeclarationContextData(
      rootClassName = rootClassName,
      declaration = declaration,
      properties = properties,
      avroPoetTypes = avroPoetTypes,
      processors = processors,
      strategies = strategies,
      isRoot = true
    )
  }
}
