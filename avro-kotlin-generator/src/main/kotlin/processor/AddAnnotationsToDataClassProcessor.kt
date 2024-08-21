package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.poet.GeneratedAnnotation
import io.toolisticon.kotlin.avro.generator.poet.SerializableAnnotation
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.builder.KotlinDataClassSpecBuilder

@OptIn(ExperimentalKotlinPoetApi::class)
class AddAnnotationsToDataClassProcessor : AbstractDataClassFromRecordTypeProcessor() {

  override fun invoke(context: SchemaDeclarationContext, input: RecordType?, builder: KotlinDataClassSpecBuilder): KotlinDataClassSpecBuilder = builder.apply {

    addAnnotation(GeneratedAnnotation(value = AvroKotlinGenerator::class.qualifiedName!!))
    addAnnotation(SerializableAnnotation())

  }

  override fun test(ctx: SchemaDeclarationContext, input: Any?): Boolean {
    return super.test(ctx, input) && ctx.isRoot
  }
}
