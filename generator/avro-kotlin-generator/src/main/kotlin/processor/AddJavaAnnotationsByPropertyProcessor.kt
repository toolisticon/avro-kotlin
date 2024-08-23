package io.toolisticon.kotlin.avro.generator.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContextBak
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.asClassName
import io.toolisticon.kotlin.avro.generator.api.processor.DataClassParameterSpecProcessor
import io.toolisticon.kotlin.avro.generator.api.processor.TypeSpecProcessor
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi.Order.DEFAULT_ORDER
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.avro.value.ObjectProperties
import io.toolisticon.kotlin.avro.value.property.JavaAnnotationProperty
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildAnnotation
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertySpecBuilder
import io.toolisticon.kotlin.generation.builder.KotlinGeneratorTypeSpecBuilder
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpec

/**
 * Reads the `javaAnnotation` property that is also supported by the avro java compiler and adds
 * the given annotations to type or param.
 */
class AddJavaAnnotationsByPropertyProcessor : TypeSpecProcessor, DataClassParameterSpecProcessor {
  companion object {

    fun createAnnotationSpec(javaAnnotation: JavaAnnotationProperty): KotlinAnnotationSpec = buildAnnotation(javaAnnotation.canonicalName.asClassName()) {
      if (javaAnnotation.members.isNotEmpty()) {
        addMember(CodeBlock.of("%L", javaAnnotation.membersString))
      }
    }

    fun createAnnotationSpecs(objectProperties: ObjectProperties): List<KotlinAnnotationSpec> = JavaAnnotationProperty.from(objectProperties)
      .map(AddJavaAnnotationsByPropertyProcessor::createAnnotationSpec)
  }

  override val order = DEFAULT_ORDER


  override val processDataClassParameterSpecPredicate: (AvroDeclarationContextBak, RecordField) -> Boolean = { _, field ->
    field.properties.contains(JavaAnnotationProperty.PROPERTY_KEY)
  }


  override fun processDataClassParameterSpec(ctx: AvroDeclarationContextBak, field: RecordField, builder: KotlinConstructorPropertySpecBuilder) {
    createAnnotationSpecs(field.properties).forEach {
      builder.addAnnotation(it)
    }
  }

  override fun processTypeSpec(ctx: AvroDeclarationContextBak, type: AvroNamedType, typeSpecClassName: ClassName, builder: KotlinGeneratorTypeSpecBuilder<*, *>) {
    createAnnotationSpecs(type.properties).forEach {
      builder.builder {
        addAnnotation(it.get())
      }
    }
  }
}
