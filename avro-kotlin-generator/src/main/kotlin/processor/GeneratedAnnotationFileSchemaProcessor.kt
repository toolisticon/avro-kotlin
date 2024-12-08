package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation

/**
 * Add Generated Annotation to generated files.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class GeneratedAnnotationFileSchemaProcessor : SchemaFileSpecProcessor() {
  override fun invoke(context: SchemaDeclarationContext, builder: KotlinFileSpecBuilder): KotlinFileSpecBuilder {
    return builder
      .addAnnotation(
        GeneratedAnnotation(AvroKotlinGenerator.NAME)
          .date(context.properties.nowSupplier())
      )
  }

  override fun test(context: SchemaDeclarationContext, input: Any): Boolean = super.test(context, input)
}
