@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.strategy

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.addKDoc
import io.toolisticon.kotlin.avro.generator.spi.SchemaDeclarationContext
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.builder.enumClassBuilder
import io.toolisticon.kotlin.generation.builder.KotlinEnumClassSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinEnumClassSpec

class NestedEnumClassStrategy : AvroEnumTypeSpecStrategy() {
  override fun invoke(context: SchemaDeclarationContext, input: EnumType): KotlinEnumClassSpec {
    val type = context[input.hashCode]
    val className = type.suffixedTypeName as ClassName

    val enumBuilder = enumClassBuilder(className).apply {
      this.addKDoc(input.documentation)
    }

    input.symbols.forEach(enumBuilder::addEnumConstant)

    return enumBuilder.build()
  }
}
