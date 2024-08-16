package io.toolisticon.kotlin.avro.generator.spi

import io.toolisticon.kotlin.avro.generator.logical.LogicalTypeMap
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpi
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpiRegistry
import kotlin.reflect.KClass

class AvroCodeGenerationSpiRegistry(registry: KotlinCodeGenerationSpiRegistry) : KotlinCodeGenerationSpiRegistry by registry{
  override val contextTypeUpperBound: KClass<*> = Any::class

  val logicalTypes: LogicalTypeMap = LogicalTypeMap(this)
}
