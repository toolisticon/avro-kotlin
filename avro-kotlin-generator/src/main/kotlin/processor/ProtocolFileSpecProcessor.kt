package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.spi.processor.KotlinFileSpecEmptyInputProcessor

/**
 * Process files created from [ProtocolDeclarationContext].
 */
@OptIn(ExperimentalKotlinPoetApi::class)
abstract class ProtocolFileSpecProcessor : KotlinFileSpecEmptyInputProcessor<ProtocolDeclarationContext>(contextType = ProtocolDeclarationContext::class) {
  abstract override fun invoke(context: ProtocolDeclarationContext, builder: KotlinFileSpecBuilder): KotlinFileSpecBuilder
  override fun test(context: ProtocolDeclarationContext, input: Any): Boolean = super.test(context, input)
}
