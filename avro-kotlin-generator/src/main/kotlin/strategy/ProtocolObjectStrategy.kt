package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.TypeName
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.asClassName
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext.Companion.toSchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.fileBuilder
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.objectBuilder
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spec.KotlinGeneratorTypeSpec
import io.toolisticon.kotlin.generation.spec.KotlinObjectSpec
import io.toolisticon.kotlin.generation.spi.strategy.executeAll
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation

@ExperimentalKotlinPoetApi
class ProtocolObjectStrategy : AvroFileSpecFromProtocolDeclarationStrategy() {
  override val order: Int = 0

  override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec {
    val protocol = input.protocol
    val className =  protocol.canonicalName.asClassName()

    val builder = objectBuilder(className).apply {
      addAnnotation(GeneratedAnnotation(value = AvroKotlinGenerator.NAME))
      addKDoc(protocol.documentation)
    }

    val schemaDeclarationContext = context.toSchemaDeclarationContext()

    val subTypesToGenerate = schemaDeclarationContext.avroPoetTypes.filter {
      it.avroType is AvroNamedType
    }.map { it.avroType as AvroNamedType to it.typeName }


    val typeSpecs: List<KotlinGeneratorTypeSpec<*>> = subTypesToGenerate.flatMap<Pair<AvroNamedType, TypeName>, KotlinGeneratorTypeSpec<*>> { (type, _) ->
      when (type) {
        is RecordType -> schemaDeclarationContext.dataClassStrategies.executeAll(schemaDeclarationContext, type)
        is EnumType -> schemaDeclarationContext.enumClassStrategies.executeAll(schemaDeclarationContext, type)
        is ErrorType -> emptyList() // TODO("fixed is not implemented yet")
        is FixedType -> emptyList() // TODO("fixed is not implemented yet")
      }
    }

    typeSpecs.forEach(builder::addType)

    val file = fileBuilder(className)
    file.addType(builder.build())

    return file.build()
  }

}
