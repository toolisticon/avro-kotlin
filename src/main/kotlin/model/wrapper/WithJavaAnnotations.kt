package io.toolisticon.avro.kotlin.model.wrapper

import io.toolisticon.avro.kotlin.value.JavaAnnotation

/**
 * Marks avro elements which `objectProps` might contain a list of `javaAnnotation`s and provides access to them.
 *
 */
interface WithJavaAnnotations {

  /**
   * @return list of [JavaAnnotation]s defined on this element, defaults to empty
   */
  fun javaAnnotations() : List<JavaAnnotation> = emptyList()
}
