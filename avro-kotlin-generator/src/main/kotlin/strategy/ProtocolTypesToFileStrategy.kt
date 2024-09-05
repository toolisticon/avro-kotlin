package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.TypeName
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext.Companion.toSchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.strategy.internal.KotlinErrorTypeStrategy
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.avro.model.ErrorType
import io.toolisticon.kotlin.avro.model.FixedType
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.spec.KotlinFileSpec
import io.toolisticon.kotlin.generation.spec.KotlinGeneratorTypeSpec
import io.toolisticon.kotlin.generation.spi.strategy.executeAll
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation
import kotlin.collections.forEach

@OptIn(ExperimentalKotlinPoetApi::class)
class ProtocolTypesToFileStrategy : AvroFileSpecFromProtocolDeclarationStrategy() {
  override fun invoke(context: ProtocolDeclarationContext, input: ProtocolDeclaration): KotlinFileSpec {
    val protocol = input.protocol
    val fileBuilder = KotlinCodeGeneration.builder.fileBuilder(className = KotlinCodeGeneration.className(input.canonicalName.namespace.value, "protocol-types")).apply {
      addAnnotation(GeneratedAnnotation(AvroKotlinGenerator.NAME))
    }


    val schemaDeclarationContext = context.toSchemaDeclarationContext()

    val subTypesToGenerate = schemaDeclarationContext.avroPoetTypes.filter {
      it.avroType is AvroNamedType
    }.distinctBy { it.avroType.hashCode }.map { it.avroType as AvroNamedType to it.typeName }


    val typeSpecs: List<KotlinGeneratorTypeSpec<*>> = subTypesToGenerate.flatMap<Pair<AvroNamedType, TypeName>, KotlinGeneratorTypeSpec<*>> { (type, _) ->
      when (type) {
        is RecordType -> schemaDeclarationContext.dataClassStrategies.executeAll(schemaDeclarationContext, type)
        is EnumType -> schemaDeclarationContext.enumClassStrategies.executeAll(schemaDeclarationContext, type)
        is ErrorType -> listOf(KotlinErrorTypeStrategy().execute(schemaDeclarationContext, type)).filterNotNull()
        is FixedType -> emptyList() // TODO("fixed is not implemented yet")
      }
    }

    typeSpecs.forEach(fileBuilder::addType)

    return fileBuilder.build()
  }
}
