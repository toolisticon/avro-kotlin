package io.holixon.axon.avro.generator.strategy

import _ktx.StringKtx.firstUppercase
import com.squareup.kotlinpoet.*
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.asClassName
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.strategy.AvroFileSpecFromProtocolDeclarationStrategy
import io.toolisticon.kotlin.avro.value.Documentation
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildFun
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.objectBuilder
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation
import mu.KLogging
import org.axonframework.commandhandling.CommandHandler

@OptIn(ExperimentalKotlinPoetApi::class)
class AxonEventSourcingHandlerProtocolInterfaceStrategy : AvroFileSpecFromProtocolDeclarationStrategy() {

  companion object : KLogging()

  override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec {

    val fileName: ClassName = (input.canonicalName.namespace + Name(input.name.value + "CommandHandlerProtocol")).asClassName()
    val fileBuilder = builder.fileBuilder(fileName)

    val objectBuilder = objectBuilder(fileName).apply {
      addAnnotation(GeneratedAnnotation(value = AvroKotlinGenerator.NAME))
      addKDoc(input.documentation)
    }

    val allCommandHandlersInterfaceName = (input.canonicalName.namespace + Name(input.name.value + "AllCommandHandlers")).asClassName()
    val allCommandHandlersInterfaceBuilder = builder.interfaceBuilder(allCommandHandlersInterfaceName).apply {
      addKDoc(Documentation("Union interface for all command handlers"))
      // TODO: introduce scopes in meta, to allow correct grouping, instead of building all command handlers for the context
    }

    /*
    Single interface for each query
     */
    input.protocol.messages
      .filterValues { message -> message.isDecider() }
      .mapNotNull { (name, message) ->

        if (message.request.fields.size == 1) {
          val commandHandlerInterfaceName = (input.canonicalName.namespace + Name(name.value.firstUppercase())).asClassName()
          val interfaceBuilder = builder.interfaceBuilder(commandHandlerInterfaceName).apply {
            // TODO: the strategy should be a fall-through in order: on message, on message type, on referenced-type
            addKDoc(message.documentation)
          }

          val function = buildFun(name.value) {
            addModifiers(KModifier.ABSTRACT)
            addAnnotation(CommandHandler::class)
            message.request.fields.forEach { f ->
              this.addParameter(f.name.value, context.avroPoetTypes[f.schema.hashCode].typeName)
            }

            // TODO: think of a special command handler for aggregate construction

          }
          interfaceBuilder.addFunction(function) // add function to the interface

        } else {
          logger.warn { "Skipped command handler definition $name, because it had more then one parameter, but at most one is supported." }
          null
        }

      }.forEach { interfaceBuilder ->
        objectBuilder.addType(interfaceBuilder) // add interface to object
        allCommandHandlersInterfaceBuilder.addSuperinterface(ClassName.bestGuess(interfaceBuilder.spec().className.simpleName))
      }


    objectBuilder.addType(allCommandHandlersInterfaceBuilder) // add union interface
    fileBuilder.addType(objectBuilder)

    // TODO run processors

    return fileBuilder.build()
  }


  override fun test(context: ProtocolDeclarationContext, input: Any?): Boolean {
    return super.test(context, input) && input is ProtocolDeclaration && input.protocol.messages.values.any { message -> message.isDecider() }
  }
}
