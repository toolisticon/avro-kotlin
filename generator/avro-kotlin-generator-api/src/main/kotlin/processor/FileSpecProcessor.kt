package io.toolisticon.kotlin.avro.generator.api.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi.Order.DEFAULT_ORDER
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpiList
import io.toolisticon.kotlin.generation.builder.KotlinFileBuilder

/**
 * Gets an [AvroDeclarationContext] that is currently generated and the [FileSpec.Builder].
 *
 * When you implement a processor, extend the [AbstractTypeSpecProcessor].
 */
interface FileSpecProcessor : AvroKotlinGeneratorProcessor {
  // TODO: we should have a predicate here
  fun processTypeSpec(
    ctx: AvroDeclarationContext,
    fileSpecClassName: ClassName,
    builder: KotlinFileBuilder
  )
}

abstract class AbstractFileSpecProcessor(override val order: Int = DEFAULT_ORDER) : FileSpecProcessor

@JvmInline
value class FileSpecProcessorList(private val list: List<FileSpecProcessor>) : List<FileSpecProcessor> by list {

  companion object {
    fun of(spis: AvroKotlinGeneratorSpiList) = FileSpecProcessorList(spis.filterIsInstance(FileSpecProcessor::class))

  }


  /**
   * Execute all processors if predicate allows it.
   */
  operator fun invoke(
    ctx: AvroDeclarationContext,
    fileSpecClassName: ClassName,
    builder: KotlinFileBuilder
  ) = forEach {
    it.processTypeSpec(ctx, fileSpecClassName, builder)
  }
}
