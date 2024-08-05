package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContextBak
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.processor.AbstractFileSpecProcessor
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.support.SuppressAnnotation

class SuppressWarningsOnFileSpecProcessor(
  private val suppressions: List<String>
) : AbstractFileSpecProcessor() {
  companion object {
    val DEFAULT_TYPES = listOf(SuppressAnnotation.REDUNDANT_VISIBILITY_MODIFIER)
  }

  /**
   * Default, used by SPI.
   */
  constructor() : this(DEFAULT_TYPES)

  override fun processTypeSpec(ctx: AvroDeclarationContextBak, fileSpecClassName: ClassName, builder: KotlinFileSpecBuilder) {
    addSuppressAnnotation(ctx.properties, builder)
  }

  fun addSuppressAnnotation(properties: AvroKotlinGeneratorProperties, builder: KotlinFileSpecBuilder) {
    if (properties.suppressRedundantModifiers) {
      builder.addAnnotation(SuppressAnnotation(suppressions))
    }
  }
}
