package io.toolisticon.kotlin.avro.generator.spi

import io.toolisticon.kotlin.avro.generator.api.processor.*
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpiList
import io.toolisticon.kotlin.avro.generator.api.strategy.AvroKotlinGeneratorStrategies
import io.toolisticon.kotlin.avro.generator.api.strategy.GenerateDataClassStrategy
import io.toolisticon.kotlin.avro.generator.api.strategy.GenerateEnumClassStrategy
import java.util.*

class AvroKotlinGeneratorServiceLoader(
  // strategies
  override val generateDataClassStrategy: GenerateDataClassStrategy,
  override val generateEnumClassStrategy: GenerateEnumClassStrategy,

  // processors
  override val logicalTypes: LogicalTypeMap,
  override val dataClassParameterSpecProcessors: DataClassParameterSpecProcessorList,
  override val typeSpecProcessors: TypeSpecProcessorList,
  override val fileSpecProcessors: FileSpecProcessorList,

  ) : AvroKotlinGeneratorProcessors, AvroKotlinGeneratorStrategies {
  companion object {
    fun load(): AvroKotlinGeneratorServiceLoader {
      val spis: AvroKotlinGeneratorSpiList = AvroKotlinGeneratorSpiList(ServiceLoader.load(AvroKotlinGeneratorSpi::class.java)
        .toList()
        .sortedBy { it.order })

      return AvroKotlinGeneratorServiceLoader(
        // strategies
        generateDataClassStrategy = spis.filterIsInstance(GenerateDataClassStrategy::class)
          .first(),
        generateEnumClassStrategy = spis.filterIsInstance(GenerateEnumClassStrategy::class)
          .first(),

        // processors
        logicalTypes = LogicalTypeMap(spis),
        dataClassParameterSpecProcessors = DataClassParameterSpecProcessorList.of(spis),
        typeSpecProcessors = TypeSpecProcessorList.of(spis),
        fileSpecProcessors = FileSpecProcessorList.of(spis)
      )
    }
  }
}
