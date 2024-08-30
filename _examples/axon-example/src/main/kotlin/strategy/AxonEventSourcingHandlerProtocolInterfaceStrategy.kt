package io.holixon.axon.avro.generator.strategy

import _ktx.StringKtx.firstUppercase
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
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
import org.axonframework.eventsourcing.EventSourcingHandler

@OptIn(ExperimentalKotlinPoetApi::class)
class AxonEventSourcingHandlerProtocolInterfaceStrategy : AvroFileSpecFromProtocolDeclarationStrategy() {

  companion object : KLogging()

  override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec {

    val fileName: ClassName = (input.canonicalName.namespace + Name(input.name.value + "EventSourcingHandlerProtocol")).asClassName()
    val fileBuilder = builder.fileBuilder(fileName)

    val objectBuilder = objectBuilder(fileName).apply {
      addAnnotation(GeneratedAnnotation(value = AvroKotlinGenerator.NAME))
      addKDoc(input.documentation)
    }

    val allEventSourcingHandlersInterfaceName = (input.canonicalName.namespace + Name(input.name.value + "AllEventSourcingHandlers")).asClassName()
    val allEventSourcingHandlersInterfaceBuilder = builder.interfaceBuilder(allEventSourcingHandlersInterfaceName).apply {
      addKDoc(Documentation("Union interface for all event souring handlers"))
      // TODO: introduce scopes in meta, to allow correct grouping, instead of building all command handlers for the context
    }

    /*
    Single interface for each event souring handler
     */
    input.protocol.messages
      .filterValues { message -> message.isDecider() }
      .mapNotNull { (name, message) ->

        val eventSouringHandlerInterfaceName = (input.canonicalName.namespace + Name(name.value.firstUppercase() + "EventSouringHandler")).asClassName()
        val interfaceBuilder = builder.interfaceBuilder(eventSouringHandlerInterfaceName).apply {
          // TODO: the strategy should be a fall-through in order: on message, on message type, on referenced-type
          addKDoc(message.documentation)
        }

        val eventType = context.avroPoetTypes[message.response.schema.hashCode]
        val function = buildFun("on" + eventType.avroType.name) {
          addModifiers(KModifier.ABSTRACT)
          addAnnotation(EventSourcingHandler::class)
          this.addParameter("event", eventType.typeName)

          // TODO: think of a special command handler for aggregate construction

        }
        interfaceBuilder.addFunction(function) // add function to the interface

      }.forEach { interfaceBuilder ->
        objectBuilder.addType(interfaceBuilder) // add interface to object
        allEventSourcingHandlersInterfaceBuilder.addSuperinterface(ClassName.bestGuess(interfaceBuilder.spec().className.simpleName))
      }


    objectBuilder.addType(allEventSourcingHandlersInterfaceBuilder) // add union interface
    fileBuilder.addType(objectBuilder)

    // TODO run processors

    return fileBuilder.build()
  }


  override fun test(context: ProtocolDeclarationContext, input: Any?): Boolean {
    return super.test(context, input) && input is ProtocolDeclaration && input.protocol.messages.values.any { message -> message.isDecider() }
  }
}
