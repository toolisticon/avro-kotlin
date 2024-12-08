package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinDataClassSpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinFileSpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinFunSpecBuilder
import io.toolisticon.kotlin.generation.spi.processor.KotlinConstructorPropertySpecProcessor
import io.toolisticon.kotlin.generation.spi.processor.KotlinDataClassSpecProcessor
import io.toolisticon.kotlin.generation.spi.processor.KotlinFileSpecEmptyInputProcessor
import io.toolisticon.kotlin.generation.spi.processor.KotlinFunSpecProcessor

@OptIn(ExperimentalKotlinPoetApi::class)
abstract class KotlinDataClassFromRecordTypeProcessorBase : KotlinDataClassSpecProcessor<SchemaDeclarationContext, RecordType>(
  contextType = SchemaDeclarationContext::class,
  inputType = RecordType::class
) {
  abstract override fun invoke(context: SchemaDeclarationContext, input: RecordType, builder: KotlinDataClassSpecBuilder): KotlinDataClassSpecBuilder
  override fun test(context: SchemaDeclarationContext, input: Any): Boolean = super.test(context, input)
}

@OptIn(ExperimentalKotlinPoetApi::class)
abstract class ConstructorPropertyFromRecordFieldProcessorBase : KotlinConstructorPropertySpecProcessor<SchemaDeclarationContext, RecordField>(
  contextType = SchemaDeclarationContext::class,
  inputType = RecordField::class
) {
  abstract override fun invoke(
    context: SchemaDeclarationContext,
    input: RecordField,
    builder: KotlinConstructorPropertySpecBuilder
  ): KotlinConstructorPropertySpecBuilder

  override fun test(context: SchemaDeclarationContext, input: Any): Boolean = super.test(context, input)
}

/**
 * Process [KotlinFunSpecBuilder] when working with [AvroProtocol.Message]s.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
abstract class KotlinFunSpecFromProtocolMessageProcessor : KotlinFunSpecProcessor<ProtocolDeclarationContext, AvroProtocol.Message>(
  contextType = ProtocolDeclarationContext::class, inputType = AvroProtocol.Message::class,
) {
  abstract override fun invoke(context: ProtocolDeclarationContext, input: AvroProtocol.Message, builder: KotlinFunSpecBuilder): KotlinFunSpecBuilder

  override fun test(context: ProtocolDeclarationContext, input: Any): Boolean = super.test(context, input)
}


/**
 * Process files created from [ProtocolDeclarationContext].
 */
@OptIn(ExperimentalKotlinPoetApi::class)
abstract class ProtocolFileSpecProcessor : KotlinFileSpecEmptyInputProcessor<ProtocolDeclarationContext>(contextType = ProtocolDeclarationContext::class) {
  abstract override fun invoke(context: ProtocolDeclarationContext, builder: KotlinFileSpecBuilder): KotlinFileSpecBuilder
  override fun test(context: ProtocolDeclarationContext, input: Any): Boolean = super.test(context, input)
}

/**
 * Process files created from [SchemaDeclarationContext].
 */
@OptIn(ExperimentalKotlinPoetApi::class)
abstract class SchemaFileSpecProcessor : KotlinFileSpecEmptyInputProcessor<SchemaDeclarationContext>(contextType = SchemaDeclarationContext::class) {
  abstract override fun invoke(context: SchemaDeclarationContext, builder: KotlinFileSpecBuilder): KotlinFileSpecBuilder
  override fun test(context: SchemaDeclarationContext, input: Any): Boolean = super.test(context, input)
}
