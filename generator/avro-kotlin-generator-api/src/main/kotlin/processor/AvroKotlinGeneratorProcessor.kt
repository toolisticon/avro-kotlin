package io.toolisticon.kotlin.avro.generator.api.processor

import io.toolisticon.kotlin.generation.spi.processor.FileSpecProcessorList


/**
 * Contains all possible processors.
 * Filled via SPI in generator.
 */
interface AvroKotlinGeneratorProcessors {
  val logicalTypes: LogicalTypeMap
  val dataClassParameterSpecProcessors: DataClassParameterSpecProcessorList

  val typeSpecProcessors: TypeSpecProcessorList

  val fileSpecProcessors: FileSpecProcessorList<*,*>
}
