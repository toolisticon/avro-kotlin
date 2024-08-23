package io.toolisticon.kotlin.avro.generator.api.strategy

import io.toolisticon.kotlin.avro.generator.api.context.AvroDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpi
import io.toolisticon.kotlin.generation.spi.strategy.DataClassSpecStrategy

/**
 * Generates kotlin data class based on record.
 *
 * TODO: how can we have multiple strategies and pick one (by name? by predicate?)
 */
abstract class GenerateDataClassStrategy(
  override val order: Int = KotlinCodeGenerationSpi.DEFAULT_ORDER
) : DataClassSpecStrategy<AvroDeclarationContext, RecordType>(
  contextType = AvroDeclarationContext::class,
  inputType = RecordType::class
)
