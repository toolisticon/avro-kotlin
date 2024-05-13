package io.toolisticon.avro.kotlin.value.property

import io.toolisticon.avro.kotlin.value.ObjectProperties

interface AvroProperty {

  val key: String
}

/**
 * Extracts list of [JavaAnnotationProperty] from [ObjectProperties]. Defaults to empty list.
 *
 * This always returns a list, in case the definition in json is a single string, the list only contains one element.
 */
val ObjectProperties.javaAnnotations: List<JavaAnnotationProperty> get() = JavaAnnotationProperty.from(this)
