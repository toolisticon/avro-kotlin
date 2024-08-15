package io.toolisticon.kotlin.avro.generator.logical

import io.toolisticon.kotlin.avro.generator.AvroCodeGenerationSpiRegistry
import io.toolisticon.kotlin.avro.value.LogicalTypeName


@JvmInline
value class LogicalTypeMap(
  private val map: Map<LogicalTypeName, AvroKotlinLogicalTypeDefinition>
) : Map<LogicalTypeName, AvroKotlinLogicalTypeDefinition> by map {
  constructor(registry: AvroCodeGenerationSpiRegistry) : this(registry.getProcessors().filterIsInstance<AvroKotlinLogicalTypeDefinition>()
    .associateBy { it.logicalTypeName }
  )
}
