package io.toolisticon.kotlin.avro.generator.api.strategy

import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec

/**
 * Generates kotlin data class based on record.
 *
 * TODO: how can we have multiple strategies and pick one (by name? by predicate?)
 */
interface GenerateDataClassStrategy : AvroKotlinGeneratorStrategy {
  fun generateDataClass(
    ctx: AvroDeclarationContext,
    recordType: RecordType
  ): KotlinDataClassSpec
}

abstract class AbstractGenerateDataClassStrategy(
  override val order: Int = AvroKotlinGeneratorSpi.DEFAULT_ORDER
) : GenerateDataClassStrategy
