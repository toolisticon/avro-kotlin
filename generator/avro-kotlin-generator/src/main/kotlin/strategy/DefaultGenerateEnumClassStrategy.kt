package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.kdoc
import io.toolisticon.kotlin.avro.generator.api.strategy.AbstractGenerateEnumClassStrategy
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.generation.builder.KotlinEnumClassBuilder
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec

class DefaultGenerateEnumClassStrategy : AbstractGenerateEnumClassStrategy() {

  override fun generateEnumClass(
    ctx: AvroDeclarationContext,
    enumType: EnumType
  ): KotlinEnumClassSpec {
    val type = ctx[enumType.hashCode]
    val className = type.suffixedTypeName as ClassName

    val enumBuilder = KotlinEnumClassBuilder.builder(className)
      .addKdoc(enumType.kdoc())

    enumType.symbols.forEach(enumBuilder::addEnumConstant)


    ctx.processors.typeSpecProcessors(ctx, enumType, className, enumBuilder)

    return enumBuilder.build()
  }
}
