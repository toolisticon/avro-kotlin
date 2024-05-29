package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.kdoc
import io.toolisticon.kotlin.avro.generator.api.ProtocolDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.strategy.AbstractGenerateDataClassStrategy
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContextData
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.generation.builder.KotlinDataClassBuilder
import io.toolisticon.kotlin.generation.builder.KotlinParameterBuilder
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec

/**
 *
 */
class DefaultGenerateDataClassStrategy : AbstractGenerateDataClassStrategy() {

  override fun generateDataClass(
    ctx: AvroDeclarationContext,
    recordType: RecordType
  ): KotlinDataClassSpec = when (ctx) {
    is SchemaDeclarationContext -> generateDataClass(ctx, recordType)
    is ProtocolDeclarationContext -> generateDataClass(ctx, recordType)
    else -> throw IllegalStateException("has to be one of SchemaDeclaration or ProtocolDeclaration")
  }

  fun generateDataClass(ctx: SchemaDeclarationContext, recordType: RecordType): KotlinDataClassSpec {
    val className: ClassName = if (ctx.isRoot) ctx.rootClassName else ctx.className(recordType.hashCode)

    val dataClassBuilder = KotlinDataClassBuilder.builder(className)


    val parameterSpecs = recordType.fields.map { field ->
      val typeName = ctx[field.hashCode].suffixedTypeName

      KotlinParameterBuilder.builder(field.name.value, typeName).apply {
        ctx.processors.dataClassParameterSpecProcessors(ctx, field, this)
      }
    }

    if (ctx.isRoot) {
      generateSubTypes(ctx.nonRoot(), dataClassBuilder)
    }

    ctx.processors.typeSpecProcessors(ctx, recordType, className, dataClassBuilder)

    parameterSpecs.forEach(dataClassBuilder::parameter)

    return dataClassBuilder.build()
  }

  fun generateDataClass(ctx: ProtocolDeclarationContext, recordType: RecordType): KotlinDataClassSpec {
    val className = ctx.className(recordType.hashCode)

    val dataClassBuilder = KotlinDataClassBuilder.builder(className)
      .addKdoc(recordType.kdoc())

    val parameterSpecs = recordType.fields.map { field ->
      val typeName = ctx.get(field.hashCode).suffixedTypeName
      KotlinParameterBuilder.builder(field.name.value, typeName).apply {
        ctx.processors.dataClassParameterSpecProcessors(ctx, field, this)
      }
    }
    ctx.processors.typeSpecProcessors(ctx, recordType, className, dataClassBuilder)

    parameterSpecs.forEach(dataClassBuilder::parameter)

    return dataClassBuilder.build()
  }

  fun generateSubTypes(nonRootCtx: AvroDeclarationContext, dataClassBuilder: KotlinDataClassBuilder) {
    require(!nonRootCtx.isRoot) { "subTypes are non-root by definition." }

    val subTypesToGenerate = nonRootCtx.avroPoetTypes.filter {
      it.avroType is AvroNamedType
    }.map { it.avroType as AvroNamedType to it.typeName }


    subTypesToGenerate.forEach { (type, _) ->
      val subType = when (type) {
        is EnumType -> nonRootCtx.strategies.generateEnumClassStrategy.generateEnumClass(nonRootCtx, type)
        is RecordType -> nonRootCtx.strategies.generateDataClassStrategy.generateDataClass(nonRootCtx, type)
        is ErrorType -> TODO()
        is FixedType -> TODO()
      }

      dataClassBuilder.addType(subType)
    }
  }


  private fun AvroDeclarationContext.nonRoot() = when (this) {
    is SchemaDeclarationContextData -> this.copy(isRoot = false)
    else -> this
  }

}
