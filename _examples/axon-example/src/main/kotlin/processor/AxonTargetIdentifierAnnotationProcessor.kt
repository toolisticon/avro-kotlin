@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.holixon.axon.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.holixon.axon.avro.generator.meta.FieldMetaData.Companion.fieldMetaData
import io.holixon.axon.avro.generator.meta.FieldMetaDataType
import io.holixon.axon.avro.generator.support.TargetAggregateIdentifierAnnotation
import io.toolisticon.kotlin.avro.generator.processor.ConstructorPropertyFromRecordFieldProcessorBase
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder

class AxonTargetIdentifierAnnotationProcessor : ConstructorPropertyFromRecordFieldProcessorBase() {
  override fun invoke(context: SchemaDeclarationContext, input: RecordField?, builder: KotlinConstructorPropertySpecBuilder): KotlinConstructorPropertySpecBuilder  = builder.apply {
    requireNotNull(input)
    addAnnotation(TargetAggregateIdentifierAnnotation)
  }

  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean {
    return super.test(ctx, input) && input is RecordField && input.fieldMetaData()?.type == FieldMetaDataType.IdentifierRef
  }
}
