@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.asClassName
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildFun
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation

class ProtocolInterfaceStrategy  : AvroFileSpecFromProtocolDeclarationStrategy() {

  override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec {
    val fileName: ClassName = (input.canonicalName.namespace + Name(input.name.value + "Interface")).asClassName()

    val builder = builder.interfaceBuilder(fileName).apply {
      addKDoc(input.protocol.documentation)
      addAnnotation(GeneratedAnnotation(value = AvroKotlinGenerator.NAME))
    }

    input.protocol.messages.values.sortedBy { it.name }.forEach { msg ->

      val function = buildFun(msg.name.value) {
        addModifiers(KModifier.ABSTRACT)

        msg.request.fields.forEach { f ->
          this.addParameter(f.name.value, context.avroPoetTypes[f.schema.hashCode].typeName)
        }
      }

      builder.addFunction(function)
    }

    return KotlinCodeGeneration.buildFile(fileName) {
      // TODO run processors
      addType(builder.build())
    }
  }

  override fun test(context: ProtocolDeclarationContext, input: Any?): Boolean {
    return super.test(context, input) && input is ProtocolDeclaration && input.protocol.messages.isNotEmpty()
  }
}
