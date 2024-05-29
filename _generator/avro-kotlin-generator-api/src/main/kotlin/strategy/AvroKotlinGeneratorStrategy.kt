package io.toolisticon.kotlin.avro.generator.api.strategy

import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi

/**
 * Root marker interface for all strategies.
 */
sealed interface AvroKotlinGeneratorStrategy : AvroKotlinGeneratorSpi

/**
 * Lists all possible strategies, so the service loader in impl can provide them to api.
 */
interface AvroKotlinGeneratorStrategies {
  val generateDataClassStrategy : GenerateDataClassStrategy
  val generateEnumClassStrategy: GenerateEnumClassStrategy
}
