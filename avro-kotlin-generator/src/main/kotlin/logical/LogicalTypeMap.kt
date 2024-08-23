package io.toolisticon.kotlin.avro.generator.logical

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.spi.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.value.LogicalTypeName


@OptIn(ExperimentalKotlinPoetApi::class)
@JvmInline
value class LogicalTypeMap(
  private val map: Map<LogicalTypeName, AvroKotlinLogicalTypeDefinition>
) : Map<LogicalTypeName, AvroKotlinLogicalTypeDefinition> by map {
  constructor(registry: AvroCodeGenerationSpiRegistry) : this(registry.processors.filterIsInstance<AvroKotlinLogicalTypeDefinition>()
    .associateBy { it.logicalTypeName }
  )
}
