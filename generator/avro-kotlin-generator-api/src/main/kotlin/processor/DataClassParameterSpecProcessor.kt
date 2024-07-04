package io.toolisticon.kotlin.avro.generator.api.processor

import com.squareup.kotlinpoet.ParameterSpec
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpiList
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinParameterSpecBuilder

/**
 * Process [ParameterSpec.Builder]s in the context of a data class.
 *
 * Mainly used to enrich parameters with annotations (like serializeWith, ...).
 */
interface DataClassParameterSpecProcessor : AvroKotlinGeneratorProcessor {

  val processDataClassParameterSpecPredicate: (AvroDeclarationContext, RecordField) -> Boolean

  fun processDataClassParameterSpec(ctx: AvroDeclarationContext, field: RecordField, builder: KotlinConstructorPropertySpecBuilder)

}

@JvmInline
value class DataClassParameterSpecProcessorList(private val list: List<DataClassParameterSpecProcessor>) : List<DataClassParameterSpecProcessor> by list {
  companion object {
    fun of(spis: AvroKotlinGeneratorSpiList) = DataClassParameterSpecProcessorList(spis.filterIsInstance(DataClassParameterSpecProcessor::class))
  }


  /**
   * Execute all processors if predicate allows it.
   */
  operator fun invoke(ctx: AvroDeclarationContext, field: RecordField, builder: KotlinConstructorPropertySpecBuilder) = forEach {
    if (it.processDataClassParameterSpecPredicate(ctx, field)) {
      it.processDataClassParameterSpec(ctx, field, builder)
    }
  }
}
