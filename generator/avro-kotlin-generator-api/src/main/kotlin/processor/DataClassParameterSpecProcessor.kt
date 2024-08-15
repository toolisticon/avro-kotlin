package io.toolisticon.kotlin.avro.generator.api.processor

import io.toolisticon.kotlin.avro.generator.api.context.SchemaDeclarationContext
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

