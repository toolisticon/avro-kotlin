package io.toolisticon.kotlin.avro.value.property

import io.toolisticon.kotlin.avro.value.ObjectProperties
import org.apache.avro.JsonProperties

interface AvroProperty {
  val key: String
}

fun interface AvroPropertySupplier<T> {
  fun from(properties: ObjectProperties) : T
  fun from(jsonProperties: JsonProperties?) : T = from(ObjectProperties.ofNullable(jsonProperties))
}

/**
 * Extracts list of [JavaAnnotationProperty] from [ObjectProperties]. Defaults to empty list.
 *
 * This always returns a list, in case the definition in json is a single string, the list only contains one element.
 */
val ObjectProperties.javaAnnotations: List<JavaAnnotationProperty> get() = JavaAnnotationProperty.from(this)
