package io.toolisticon.kotlin.avro.generator.api.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import io.toolisticon.kotlin.avro.generator.api.context.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpiList
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.generation.builder.KotlinGeneratorTypeSpecBuilder

/**
 * Gets an [AvroNamedType] that is currently generated and the [TypeSpec.Builder].
 *
 * When you implement a processor, extend the [AbstractTypeSpecProcessor].
 */
interface TypeSpecProcessor : AvroKotlinGeneratorProcessor {
  // TODO: we should have a predicate here
  fun processTypeSpec(
    ctx: AvroDeclarationContext,
    type: AvroNamedType,
    typeSpecClassName: ClassName,
    builder: KotlinGeneratorTypeSpecBuilder<*, *>
  )
}

abstract class AbstractTypeSpecProcessor(override val order: Int = DEFAULT_ORDER) : TypeSpecProcessor

@JvmInline
value class TypeSpecProcessorList(private val list: List<TypeSpecProcessor>) : List<TypeSpecProcessor> by list {

  companion object {
    fun of(spis: AvroKotlinGeneratorSpiList) = TypeSpecProcessorList(spis.filterIsInstance(TypeSpecProcessor::class))
  }




  /**
   * Execute all processors if predicate allows it.
   */
  operator fun invoke(
    ctx: AvroDeclarationContext,
    type: AvroNamedType,
    typeSpecClassName: ClassName,
    builder: KotlinGeneratorTypeSpecBuilder<*, *>
  ) = forEach {
    it.processTypeSpec(ctx, type, typeSpecClassName, builder)
  }
}
