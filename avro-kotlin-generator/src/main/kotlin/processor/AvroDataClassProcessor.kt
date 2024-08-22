package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.generation.builder.KotlinDataClassSpecBuilder
import io.toolisticon.kotlin.generation.spi.processor.DataClassSpecProcessor

@OptIn(ExperimentalKotlinPoetApi::class)
class AvroDataClassProcessor : DataClassSpecProcessor<SchemaDeclarationContext, RecordField>(
  contextType = SchemaDeclarationContext::class, inputType = RecordField::class,
) {
  override fun invoke(context: SchemaDeclarationContext, input: RecordField?, builder: KotlinDataClassSpecBuilder): KotlinDataClassSpecBuilder {
    TODO("Not yet implemented")
  }

}
