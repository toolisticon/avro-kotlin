package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation

/**
 * Add Generated Annotation to generated files.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class GeneratedAnnotationFileProtocolProcessor : ProtocolFileSpecProcessor() {
  override fun invoke(context: ProtocolDeclarationContext, builder: KotlinFileSpecBuilder): KotlinFileSpecBuilder {
    return builder
      .addAnnotation(
        GeneratedAnnotation(AvroKotlinGenerator.NAME)
          .date(context.properties.nowSupplier())
      )
  }

  override fun test(context: ProtocolDeclarationContext, input: Any): Boolean = super.test(context, input)
}
