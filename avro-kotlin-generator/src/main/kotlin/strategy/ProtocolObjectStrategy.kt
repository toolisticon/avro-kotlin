@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.TypeName
import io.toolisticon.kotlin.avro.declaration.SchemaDeclaration
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.asClassName
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.ProtocolDeclarationContext.Companion.toSchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec
import io.toolisticon.kotlin.generation.spec.KotlinGeneratorTypeSpec
import io.toolisticon.kotlin.generation.spec.KotlinObjectSpec
import io.toolisticon.kotlin.generation.spi.strategy.KotlinObjectSpecStrategy
import io.toolisticon.kotlin.generation.spi.strategy.executeAll
import io.toolisticon.kotlin.generation.support.GeneratedAnnotation

class ProtocolObjectStrategy : KotlinObjectSpecStrategy<ProtocolDeclarationContext, AvroProtocol>(
  contextType = ProtocolDeclarationContext::class, inputType = AvroProtocol::class
) {
  override fun invoke(context: ProtocolDeclarationContext, input: AvroProtocol): KotlinObjectSpec {

    val builder = KotlinCodeGeneration.builder.objectBuilder(input.canonicalName.asClassName())
      .addAnnotation(GeneratedAnnotation(value = AvroKotlinGenerator.NAME))

    builder.addKDoc(input.documentation)

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

    typeSpecs.forEach {
      when (it) {
        is KotlinDataClassSpec -> builder.addType(it)
        is KotlinEnumClassSpec -> builder.addType(it)
        else -> TODO("not implemented, FIXME")
      }
    }

    return builder.build()
  }
}
