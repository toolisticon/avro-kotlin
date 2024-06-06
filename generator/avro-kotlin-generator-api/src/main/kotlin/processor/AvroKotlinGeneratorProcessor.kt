package io.toolisticon.kotlin.avro.generator.api.processor

import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi

/**
 * Contains all possible processors.
 * Filled via SPI in generator.
 */
interface AvroKotlinGeneratorProcessors {
  val logicalTypes: LogicalTypeMap
  val dataClassParameterSpecProcessors: DataClassParameterSpecProcessorList

  val typeSpecProcessors: TypeSpecProcessorList

  val fileSpecProcessors: FileSpecProcessorList
}

/**
 * Root interface of all processors. Used to load all implementations
 * via ServiceLoader/SPI.
 */
sealed interface AvroKotlinGeneratorProcessor : AvroKotlinGeneratorSpi
