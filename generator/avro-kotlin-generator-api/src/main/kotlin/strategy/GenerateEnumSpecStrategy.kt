package io.toolisticon.kotlin.avro.generator.api.strategy

import io.toolisticon.kotlin.avro.generator.api.context.AvroDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpi
import io.toolisticon.kotlin.generation.spi.strategy.DataClassSpecStrategy


abstract class GenerateEnumClassStrategy(
  override val order: Int = KotlinCodeGenerationSpi.DEFAULT_ORDER
) : DataClassSpecStrategy<AvroDeclarationContext, RecordType>(AvroDeclarationContext::class, RecordType::class)
