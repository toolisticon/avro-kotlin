package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.AnnotationSpec.UseSiteTarget
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.processor.AbstractFileSpecProcessor
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpec

class SuppressWarningsOnFileSpecProcessor(
  private val suppressions: List<String>
) : AbstractFileSpecProcessor() {
  companion object {
    val DEFAULT_TYPES = listOf("RedundantVisibilityModifier")
  }

  /**
   * Default, used by SPI.
   */
  constructor() : this(DEFAULT_TYPES)

  override fun processTypeSpec(ctx: AvroDeclarationContext, fileSpecClassName: ClassName, builder: KotlinFileSpecBuilder) {
    addSuppressAnnotation(ctx.properties, builder)
  }

  fun addSuppressAnnotation(properties: AvroKotlinGeneratorProperties, builder: KotlinFileSpecBuilder) {
    if (properties.suppressRedundantModifiers) {
      val block = CodeBlock.builder().apply {
        suppressions.mapIndexed { index, s ->
          if (index == suppressions.lastIndex) {
            this.add("%S", s)
          } else {
            this.add("%S, ", s)
          }
        }
      }.build()


      // FIXME support in kotlin-generator
      builder.addAnnotation(
        KotlinAnnotationSpec(
          AnnotationSpec.builder(Suppress::class)
            .addMember(block)
            .useSiteTarget(UseSiteTarget.FILE)
            .build()
        )
      )
    }
  }
}
