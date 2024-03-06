package io.toolisticon.avro.kotlin.value

/**
 * A string representing a java annotation as read from additional [io.toolisticon.avro.kotlin.value.ObjectProperties].
 */
@JvmInline
value class JavaAnnotation(override val value: String) : ValueType<String> {
  companion object {
    const val PROPERTY_KEY = "javaAnnotation"
  }
}
