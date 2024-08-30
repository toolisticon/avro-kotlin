package io.holixon.axon.avro.generator.strategy

import _ktx.StringKtx.firstUppercase
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asTypeName
import io.holixon.axon.avro.generator.meta.RecordMetaData.Companion.recordMetaData
import io.holixon.axon.avro.generator.meta.RecordMetaDataType
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.asClassName
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.strategy.AvroFileSpecFromProtocolDeclarationStrategy
import io.toolisticon.kotlin.avro.model.AvroType
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.avro.value.Documentation
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildFun
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.classBuilder
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.objectBuilder
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation
import org.axonframework.queryhandling.QueryGateway

@OptIn(ExperimentalKotlinPoetApi::class)
class AxonQueryProtocolExtensionsStrategy : AvroFileSpecFromProtocolDeclarationStrategy() {

  override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec {

    val fileName: ClassName = (input.canonicalName.namespace + Name(input.name.value + "QueryGatewayExt")).asClassName()
    val fileBuilder = builder.fileBuilder(fileName)

    val objectBuilder = objectBuilder(fileName).apply {
      addAnnotation(GeneratedAnnotation(value = AvroKotlinGenerator.NAME))
      addKDoc(input.documentation)
    }

    /*
    Single interface for each query
     */
    input.protocol.messages
      .filterValues { message -> message.isQuery() }
      .map { (name, message) ->

      }.forEach { interfaceBuilder ->

      }


    fileBuilder.addType(objectBuilder)


    // TODO run processors

    return fileBuilder.build()
  }

  override fun test(context: ProtocolDeclarationContext, input: Any?): Boolean {
    return super.test(context, input) && input is ProtocolDeclaration && input.protocol.messages.values.any { message -> message.isQuery() }
  }
}
