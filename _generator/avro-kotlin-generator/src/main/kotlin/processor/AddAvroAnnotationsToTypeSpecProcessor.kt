package io.toolisticon.kotlin.avro.generator.processor


import com.github.avrokotlin.avro4k.AvroName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.avroClassName
import io.toolisticon.kotlin.avro.generator.api.processor.AbstractTypeSpecProcessor
import io.toolisticon.kotlin.avro.generator.api.spi.AvroKotlinGeneratorSpi.Order.DEFAULT_ORDER
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.generation.builder.KotlinPoetTypeSpecBuilder
import kotlinx.serialization.Serializable
import org.apache.avro.specific.AvroGenerated

class AddAvroAnnotationsToTypeSpecProcessor : AbstractTypeSpecProcessor(order = DEFAULT_ORDER) {

  override fun processTypeSpec(
    ctx: AvroDeclarationContext,
    type: AvroNamedType,
    typeSpecClassName: ClassName,
    builder: KotlinPoetTypeSpecBuilder<*>
  ) {
    builder.apply {
      addAnnotation(Serializable::class)
      addAnnotation(AvroGenerated::class)
    }
    addAvroNamedAnnotation(type, typeSpecClassName, builder)
  }

  fun addAvroNamedAnnotation(type: AvroNamedType, typeSpecClassName: ClassName, builder: KotlinPoetTypeSpecBuilder<*>) {
    val originalName = avroClassName(type).simpleName
    if (typeSpecClassName.simpleName != originalName) {
      builder.addAnnotation(
        AnnotationSpec.builder(AvroName::class)
          .addMember("%S", originalName).build()
      )
    }
  }

}
