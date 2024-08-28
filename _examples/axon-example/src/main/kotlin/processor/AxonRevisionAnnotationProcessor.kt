@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.holixon.axon.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.holixon.axon.avro.generator.meta.RecordMetaData.Companion.recordMetaData
import io.holixon.axon.avro.generator.support.RevisionAnnotation
import io.toolisticon.kotlin.avro.generator.processor.KotlinDataClassFromRecordTypeProcessorBase
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.builder.KotlinDataClassSpecBuilder

class AxonRevisionAnnotationProcessor : KotlinDataClassFromRecordTypeProcessorBase() {
  override fun invoke(context: SchemaDeclarationContext, input: RecordType?, builder: KotlinDataClassSpecBuilder): KotlinDataClassSpecBuilder {
    requireNotNull(input)

    val meta = input.schema.recordMetaData()

    builder.addAnnotation(RevisionAnnotation(meta?.revision!!))

    return builder
  }

  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean {
    return super.test(ctx, input) && input is RecordType && input.schema.recordMetaData()?.revision != null
  }
}
