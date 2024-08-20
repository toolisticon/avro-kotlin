package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.support.ContextualAnnotation

@OptIn(ExperimentalKotlinPoetApi::class)
class AddKDocAndContextualToConstructorPropertyProcessor : AbstractPropertyFromRecordFieldProcessor() {
  override fun invoke(
    context: SchemaDeclarationContext,
    input: RecordField?,
    builder: KotlinConstructorPropertySpecBuilder
  ): KotlinConstructorPropertySpecBuilder = builder.apply {
    input?.documentation?.let {
      builder.addKdoc(it.value)
    }
    if (input?.schema?.logicalType != null) {
      this.addAnnotation(ContextualAnnotation)
    }
  }
}
