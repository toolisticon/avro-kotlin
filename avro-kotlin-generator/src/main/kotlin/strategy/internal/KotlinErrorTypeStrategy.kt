package io.toolisticon.kotlin.avro.generator.strategy.internal

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.ErrorType
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.format.FORMAT_NAME
import io.toolisticon.kotlin.generation.builder.KotlinClassSpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinFunSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinClassSpec
import io.toolisticon.kotlin.generation.spi.strategy.KotlinClassSpecStrategy
import mu.KLogging
import org.apache.avro.AvroRemoteException

/**
 * Generates a class extending [kotlin.RuntimeException]. Supports error types that have a single
 * string field, which value will be used as the message of the runtime exception.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
class KotlinErrorTypeStrategy : InternalStrategy, KotlinClassSpecStrategy<SchemaDeclarationContext, ErrorType>(
  contextType = SchemaDeclarationContext::class, inputType = ErrorType::class
) {

  override fun invoke(context: SchemaDeclarationContext, input: ErrorType): KotlinClassSpec {
    require(input.fields.size == 1 && input.fields.single().schema.type == SchemaType.STRING) {
      "Generic Exceptions currently only support a single string field which is used as the message."
    }
    val type = context[input.hashCode]
    val className = type.suffixedTypeName as ClassName

    val parameterName = input.fields.single().name

    val builder = KotlinClassSpecBuilder.builder(className).apply {
      addKDoc(input.documentation)
      primaryConstructor(
        KotlinFunSpecBuilder.constructorBuilder().apply {
          addParameter(parameterName.value, String::class)
        }
      )
      superclass(AvroRemoteException::class)
      addSuperclassConstructorParameter(FORMAT_NAME, parameterName.value)
    }

    return builder.build()
  }

  override fun test(context: SchemaDeclarationContext, input: Any): Boolean {
    return super.test(context, input) && input is ErrorType
  }
}
