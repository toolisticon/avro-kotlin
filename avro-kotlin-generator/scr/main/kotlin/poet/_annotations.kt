package io.toolisticon.kotlin.avro.generator.poet

import com.github.avrokotlin.avro4k.AvroDecimal
import io.toolisticon.kotlin.avro.generator.Avro4kSerializerKClass
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildAnnotation
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpec
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpecSupplier
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Creates a [KotlinAnnotationSpec] for `@Serializable(with=MyCustomSerializer::class)` as required for
 * logical types with custom serialization.
 */
data class SerializableWithAnnotation(val serializerClass: Avro4kSerializerKClass) : KotlinAnnotationSpecSupplier {
  override fun spec(): KotlinAnnotationSpec = buildAnnotation(Serializable::class) {
    addKClassMember("with", serializerClass)
  }
}

@OptIn(ExperimentalSerializationApi::class)
data class ScalePrecisionAnnotation(val precision: Int = 0, val scale: Int = 0) : KotlinAnnotationSpecSupplier {
  override fun spec(): KotlinAnnotationSpec = buildAnnotation(AvroDecimal::class) {
    addNumberMember("precision", precision)
    addNumberMember("scale", scale)
  }
}

data class AvroNameAnnotation(val name: String) : KotlinAnnotationSpecSupplier {
  override fun spec(): KotlinAnnotationSpec = buildAnnotation(SerialName::class) {
    addStringMember("value", name)
  }
}
