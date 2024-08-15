package io.toolisticon.kotlin.avro.generator.api.processor

import io.toolisticon.kotlin.avro.generator.api.context.AvroDeclarationContext
import io.toolisticon.kotlin.generation.spi.processor.ConstructorPropertySpecProcessorList
import io.toolisticon.kotlin.generation.spi.processor.DataClassSpecProcessorList
import io.toolisticon.kotlin.generation.spi.processor.EnumClassSpecProcessorList
import io.toolisticon.kotlin.generation.spi.processor.FileSpecProcessorList


/**
 * Contains all possible processors.
 * Filled via SPI in generator.
 */
interface AvroKotlinGeneratorProcessors {
  val logicalTypes: LogicalTypeMap
  val dataClassParameterSpecProcessors: ConstructorPropertySpecProcessorList<AvroDeclarationContext, *>

  val dataClassSpecProcessors: DataClassSpecProcessorList<AvroDeclarationContext, *>
  val enumClassSpecProcessors: EnumClassSpecProcessorList<AvroDeclarationContext, *>

  val fileSpecProcessors: FileSpecProcessorList<AvroDeclarationContext, *>
}
