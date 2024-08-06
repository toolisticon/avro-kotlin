package io.toolisticon.kotlin.avro.generator.api.strategy

import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContextBak
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spi.AbstractDataClassSpecStrategy
import io.toolisticon.kotlin.generation.spi.KotlinCodeGenerationSpi

/**
 * Generates kotlin data class based on record.
 *
 * TODO: how can we have multiple strategies and pick one (by name? by predicate?)
 */
interface GenerateDataClassStrategy : AbstractDataClassSpecStrategy {
  fun generateDataClass(
    ctx: AvroDeclarationContextBak,
    recordType: RecordType
  ): KotlinDataClassSpec
}

abstract class AbstractGenerateDataClassStrategy(
  override val order: Int = KotlinCodeGenerationSpi.DEFAULT_ORDER
) : GenerateDataClassStrategy
