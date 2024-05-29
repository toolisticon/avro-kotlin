package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.processor.DataClassParameterSpecProcessor
import io.toolisticon.kotlin.avro.generator.api.processor.TypeSpecProcessor
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi.Order.DEFAULT_ORDER
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.avro.value.ObjectProperties
import io.toolisticon.kotlin.avro.value.property.JavaAnnotationProperty
import io.toolisticon.kotlin.generation.builder.KotlinParameterBuilder
import io.toolisticon.kotlin.generation.builder.KotlinPoetTypeSpecBuilder

/**
 * Reads the `javaAnnotation` property that is also supported by the avro java compiler and adds
 * the given annotations to type or param.
 */
class AddJavaAnnotationsByPropertyProcessor : TypeSpecProcessor, DataClassParameterSpecProcessor {
  companion object {
    const val PROPERTY_JAVA_ANNOTATION = "javaAnnotation"

    fun createAnnotationSpec(javaAnnotation: JavaAnnotationProperty): AnnotationSpec =
      AnnotationSpec.builder(ClassName(packageName = javaAnnotation.canonicalName.namespace.value, javaAnnotation.canonicalName.name.value))
        .apply {
          if (javaAnnotation.members.isNotEmpty()) {
            addMember(CodeBlock.of("%L", javaAnnotation.membersString))
          }
        }.build()

    fun createAnnotationSpecs(objectProperties: ObjectProperties): List<AnnotationSpec> =
      JavaAnnotationProperty.from(objectProperties).map(AddJavaAnnotationsByPropertyProcessor::createAnnotationSpec)
  }

  override val order = DEFAULT_ORDER


  override val processDataClassParameterSpecPredicate: (AvroDeclarationContext, RecordField) -> Boolean = { _, field ->
    field.properties.contains(PROPERTY_JAVA_ANNOTATION)
  }


  override fun processDataClassParameterSpec(ctx: AvroDeclarationContext, field: RecordField, builder: KotlinParameterBuilder) {
    createAnnotationSpecs(field.properties).forEach {
      builder.addAnnotation(it)
    }
  }

  override fun processTypeSpec(ctx: AvroDeclarationContext, type: AvroNamedType, typeSpecClassName: ClassName, builder: KotlinPoetTypeSpecBuilder<*>) {
    createAnnotationSpecs(type.properties).forEach {
      builder.addAnnotation(it)
    }
  }
}
