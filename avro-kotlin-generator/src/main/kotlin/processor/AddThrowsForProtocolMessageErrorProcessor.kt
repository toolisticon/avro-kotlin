package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol.Message
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isErrorType
import io.toolisticon.kotlin.generation.builder.KotlinFunSpecBuilder
import io.toolisticon.kotlin.generation.support.ThrowsAnnotation

@OptIn(ExperimentalKotlinPoetApi::class)
class AddThrowsForProtocolMessageErrorProcessor : KotlinFunSpecFromProtocolMessageProcessor() {

  override fun invoke(context: ProtocolDeclarationContext, input: Message, builder: KotlinFunSpecBuilder): KotlinFunSpecBuilder {
    requireNotNull(input)

    val exceptionType = context.avroPoetTypes[input.errors.hashCode]

    return builder.apply {
      addAnnotation(ThrowsAnnotation(exceptionType.typeName))
    }
  }

  override fun test(context: ProtocolDeclarationContext, input: Any): Boolean {
    return super.test(context, input) && input is Message && input.errors.isErrorType
  }
}
