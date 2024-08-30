package io.toolisticon.kotlin.avro.generator.poet

import com.github.avrokotlin.avro4k.AvroDecimal
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import io.toolisticon.kotlin.avro.generator.Avro4kSerializerKClass
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildAnnotation
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpec
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpecSupplier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Creates a [KotlinAnnotationSpec] for `@Serializable(with=MyCustomSerializer::class)` as required for
 * logical types with custom serialization.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
data class SerializableAnnotation(val serializerClass: Avro4kSerializerKClass? = null) : KotlinAnnotationSpecSupplier {
  override fun spec(): KotlinAnnotationSpec = buildAnnotation(Serializable::class) {
    if (serializerClass != null) {
      addKClassMember("with", serializerClass)
    }
  }
}

@OptIn(ExperimentalKotlinPoetApi::class)
data class AvroDecimalAnnotation(val precision: Int = 0, val scale: Int = 0) : KotlinAnnotationSpecSupplier {
  override fun spec(): KotlinAnnotationSpec = buildAnnotation(AvroDecimal::class) {
    addNumberMember("precision", precision)
    addNumberMember("scale", scale)
  }
}


@OptIn(ExperimentalKotlinPoetApi::class)
data class SerialNameAnnotation(val name: String) : KotlinAnnotationSpecSupplier {

  constructor(className: ClassName) : this(className.canonicalName)

  override fun spec(): KotlinAnnotationSpec = buildAnnotation(SerialName::class) {
    addStringMember("value", name)
  }
}

