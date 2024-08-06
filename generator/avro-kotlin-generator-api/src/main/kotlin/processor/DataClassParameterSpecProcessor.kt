package io.toolisticon.kotlin.avro.generator.api.processor

import io.toolisticon.kotlin.avro.generator.api.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpiList
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.spi.processor.ConstructorPropertySpecProcessor


class DataClassParameterSpecProcessor : ConstructorPropertySpecProcessor<SchemaDeclarationContext, RecordField>(
  contextType = SchemaDeclarationContext::class,
  inputType = RecordField::class
) {

  override fun invoke(
    context: SchemaDeclarationContext,
    input: RecordField?,
    builder: KotlinConstructorPropertySpecBuilder
  ): KotlinConstructorPropertySpecBuilder {
    TODO("Not yet implemented")
  }
}

@JvmInline
value class DataClassParameterSpecProcessorList(private val list: List<DataClassParameterSpecProcessor>) : List<DataClassParameterSpecProcessor> by list {
  companion object {
    fun of(spis: AvroKotlinGeneratorSpiList) = DataClassParameterSpecProcessorList(spis.filterIsInstance(DataClassParameterSpecProcessor::class))
  }


  /**
   * Execute all processors if predicate allows it.
   */
  operator fun invoke(ctx: SchemaDeclarationContext, field: RecordField, builder: KotlinConstructorPropertySpecBuilder) = forEach {
    if (it.test(ctx, field)) {
      it.invoke(ctx, field, builder)
    }
  }
}
