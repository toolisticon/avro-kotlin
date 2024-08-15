package io.toolisticon.kotlin.avro.generator.processor


import com.squareup.kotlinpoet.ClassName
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.avroClassName
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.generation.builder.KotlinGeneratorTypeSpecBuilder
import kotlinx.serialization.Serializable
import org.apache.avro.specific.AvroGenerated

class AddAvroAnnotationsToTypeSpecProcessor : AbstractTypeSpecProcessor(order = DEFAULT_ORDER) {

  override fun processTypeSpec(
      ctx: AvroDeclarationContextBak,
      type: AvroNamedType,
      typeSpecClassName: ClassName,
      builder: KotlinGeneratorTypeSpecBuilder<*, *>
  ) {
    builder.builder {
      addAnnotation(Serializable::class)
      addAnnotation(AvroGenerated::class)
    }
    addAvroNamedAnnotation(type, typeSpecClassName, builder)
  }

  fun addAvroNamedAnnotation(type: AvroNamedType, typeSpecClassName: ClassName, builder: KotlinGeneratorTypeSpecBuilder<*, *>) {
    val originalName = avroClassName(type).simpleName
    if (typeSpecClassName.simpleName != originalName) {
      builder.builder {
        addAnnotation(AvroKotlinGeneratorApi.AvroNameAnnotation(originalName).get())
      }
    }
  }

}
