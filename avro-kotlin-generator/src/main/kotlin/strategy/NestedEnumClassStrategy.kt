@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.generation.builder.KotlinEnumClassSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec

class NestedEnumClassStrategy : AvroEnumTypeSpecStrategy() {
  override fun invoke(context: SchemaDeclarationContext, input: EnumType): KotlinEnumClassSpec {
    val type = context[input.hashCode]
    val className = type.suffixedTypeName as ClassName

    val enumBuilder = KotlinEnumClassSpecBuilder.builder(className)
    input.documentation?.value?.let(enumBuilder::addKdoc)

    input.symbols.forEach(enumBuilder::addEnumConstant)

    return enumBuilder.build()
  }
}
