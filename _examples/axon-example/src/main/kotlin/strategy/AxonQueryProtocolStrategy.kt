package io.holixon.axon.avro.generator.strategy

import _ktx.StringKtx.firstUppercase
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
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
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildFun
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation

@OptIn(ExperimentalKotlinPoetApi::class)
class AxonQueryProtocolStrategy : AvroFileSpecFromProtocolDeclarationStrategy() {

  override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec {

    val fileName: ClassName = (input.canonicalName.namespace + Name(input.name.value + "QueryProtocol")).asClassName()
    val fileBuilder = builder.fileBuilder(fileName)



    input.protocol.messages.filterValues { message -> message.isQuery() }.forEach { (name, message) ->

      val queryInterfaceName = (input.canonicalName.namespace + Name(input.name.value + name.value.firstUppercase())).asClassName()

      val interfaceBuilder = builder.interfaceBuilder(queryInterfaceName).apply {
        addKDoc(message.documentation)
        addAnnotation(GeneratedAnnotation(value = AvroKotlinGenerator.NAME))
      }

      val function = buildFun(name.value) {
        addModifiers(KModifier.ABSTRACT)
        message.request.fields.forEach { f ->
          this.addParameter(f.name.value, context.avroPoetTypes[f.schema.hashCode].typeName)
        }
      }

      interfaceBuilder.addFunction(function)

      fileBuilder.addType(
        interfaceBuilder
      )
    }

    // TODO run processors
    return fileBuilder.build()
  }

  override fun test(context: ProtocolDeclarationContext, input: Any?): Boolean {
    return super.test(context, input) && input is ProtocolDeclaration && input.protocol.messages.values.any { message -> message.isQuery() }
  }


  fun AvroProtocol.Message.isQuery(): Boolean {
    return this.request.fields
      .map { AvroType.avroType<AvroType>(it.schema) }
      .filterIsInstance<RecordType>()
      .mapNotNull { it.recordMetaData() }
      .any { RecordMetaDataType.Query == it.type }
    // TODO: analyze response of query
    // RecordMetaDataType.QueryResult == it.type
  }
}
