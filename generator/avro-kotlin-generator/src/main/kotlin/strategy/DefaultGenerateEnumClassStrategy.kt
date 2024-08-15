package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContextBak
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.kdoc
import io.toolisticon.kotlin.avro.generator.api.strategy.GenerateEnumClassStrategy
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.generation.builder.KotlinEnumClassSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec

class DefaultGenerateEnumClassStrategy : GenerateEnumClassStrategy() {

  override fun generateEnumClass(
      ctx: AvroDeclarationContextBak,
      enumType: EnumType
  ): KotlinEnumClassSpec {
    val type = ctx[enumType.hashCode]
    val className = type.suffixedTypeName as ClassName

    val enumBuilder = KotlinEnumClassSpecBuilder.builder(className)
      .addKdoc(enumType.kdoc())

    enumType.symbols.forEach(enumBuilder::addEnumConstant)


    ctx.processors.typeSpecProcessors(ctx, enumType, className, enumBuilder)

    return enumBuilder.build()
  }
}
