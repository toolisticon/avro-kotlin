package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContextBak
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.kdoc
import io.toolisticon.kotlin.avro.generator.api.ProtocolDeclarationContextBak
import io.toolisticon.kotlin.avro.generator.api.SchemaDeclarationContextBak
import io.toolisticon.kotlin.avro.generator.api.strategy.GenerateDataClassStrategy
import io.toolisticon.kotlin.avro.generator.context.SchemaDeclarationContextData
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinDataClassSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinDataClassSpec

/**
 *
 */
class DefaultGenerateDataClassStrategy : GenerateDataClassStrategy() {

  override fun generateDataClass(
      ctx: AvroDeclarationContextBak,
      recordType: RecordType
  ): KotlinDataClassSpec = when (ctx) {
    is SchemaDeclarationContextBak -> generateDataClass(ctx, recordType)
    is ProtocolDeclarationContextBak -> generateDataClass(ctx, recordType)
    else -> throw IllegalStateException("has to be one of SchemaDeclaration or ProtocolDeclaration")
  }

  fun generateDataClass(ctx: SchemaDeclarationContextBak, recordType: RecordType): KotlinDataClassSpec {
    val className: ClassName = if (ctx.isRoot) ctx.rootClassName else ctx.className(recordType.hashCode)

    val dataClassBuilder = KotlinDataClassSpecBuilder.builder(className)

    val parameterSpecs = recordType.fields.map { field ->
      val typeName = ctx[field.hashCode].suffixedTypeName

      KotlinConstructorPropertySpecBuilder.builder(name = field.name.value, typeName).apply {
        ctx.processors.dataClassParameterSpecProcessors(ctx, field, this)
      }
    }

    if (ctx.isRoot) {
      generateSubTypes(ctx.nonRoot(), dataClassBuilder)
    }

    ctx.processors.typeSpecProcessors(ctx, recordType, className, dataClassBuilder)

    parameterSpecs.forEach(dataClassBuilder::addConstructorProperty)

    return dataClassBuilder.build()
  }

  fun generateDataClass(ctx: ProtocolDeclarationContextBak, recordType: RecordType): KotlinDataClassSpec {
    val className = ctx.className(recordType.hashCode)

    val dataClassBuilder = KotlinDataClassSpecBuilder.builder(className)
      .addKdoc(recordType.kdoc())

    val parameterSpecs = recordType.fields.map { field ->
      val typeName = ctx[field.hashCode].suffixedTypeName
      KotlinConstructorPropertySpecBuilder.builder(field.name.value, typeName).apply {
        ctx.processors.dataClassParameterSpecProcessors(ctx, field, this)
      }
    }
    ctx.processors.typeSpecProcessors(ctx, recordType, className, dataClassBuilder)

    parameterSpecs.forEach(dataClassBuilder::addConstructorProperty)

    return dataClassBuilder.build()
  }

  fun generateSubTypes(nonRootCtx: AvroDeclarationContextBak, dataClassBuilder: KotlinDataClassSpecBuilder) {
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


  private fun AvroDeclarationContextBak.nonRoot() = when (this) {
    is SchemaDeclarationContextData -> this.copy(isRoot = false)
    else -> this
  }

}
