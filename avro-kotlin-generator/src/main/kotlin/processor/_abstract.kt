@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinDataClassSpecBuilder
import io.toolisticon.kotlin.generation.spi.processor.KotlinConstructorPropertySpecProcessor
import io.toolisticon.kotlin.generation.spi.processor.KotlinDataClassSpecProcessor

abstract class KotlinDataClassFromRecordTypeProcessorBase : KotlinDataClassSpecProcessor<SchemaDeclarationContext, RecordType>(
  contextType = SchemaDeclarationContext::class,
  inputType = RecordType::class
) {
  abstract override fun invoke(context: SchemaDeclarationContext, input: RecordType?, builder: KotlinDataClassSpecBuilder): KotlinDataClassSpecBuilder
  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean = super.test(ctx, input)
}

abstract class ConstructorPropertyFromRecordFieldProcessorBase : KotlinConstructorPropertySpecProcessor<SchemaDeclarationContext, RecordField>(
  contextType = SchemaDeclarationContext::class,
  inputType = RecordField::class
) {
  abstract override fun invoke(
    context: SchemaDeclarationContext,
    input: RecordField?,
    builder: KotlinConstructorPropertySpecBuilder
  ): KotlinConstructorPropertySpecBuilder

  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean = super.test(ctx, input)
}
