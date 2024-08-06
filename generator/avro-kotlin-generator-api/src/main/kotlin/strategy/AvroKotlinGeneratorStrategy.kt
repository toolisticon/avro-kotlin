package io.toolisticon.kotlin.avro.generator.api.strategy

/**
 * Lists all possible strategies, so the service loader in impl can provide them to api.
 */
interface AvroKotlinGeneratorStrategies {
  val generateDataClassStrategy : GenerateDataClassStrategy
  val generateEnumClassStrategy: GenerateEnumClassStrategy
}
