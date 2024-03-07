package io.toolisticon.avro.kotlin.value.property

import _ktx.StringKtx.trimToNull
import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.ObjectProperties
import io.toolisticon.avro.kotlin.value.ValueType
import java.util.function.Supplier

/**
 * A string representing a java annotation as read from additional [ObjectProperties].
 */
@JvmInline
value class JavaAnnotationProperty private constructor(override val value: Pair<CanonicalName, Map<String, String>>) :
  ValueType<Pair<CanonicalName, Map<String, String>>>, AvroProperty {

  companion object {
    const val PROPERTY_KEY = "javaAnnotation"

    fun from(objectProperties: ObjectProperties): List<JavaAnnotationProperty> = when (val annotationsValue = objectProperties.get(PROPERTY_KEY)) {
      is String -> listOf(JavaAnnotationProperty(annotationsValue))
      is List<*> -> annotationsValue.filterIsInstance<String>().map { JavaAnnotationProperty(it) }
      else -> emptyList()
    }
  }

  constructor(annotationString: String) : this(value = Supplier<Pair<CanonicalName, Map<String, String>>> {
    val (fqn, members) = annotationString.substringBefore("(") to annotationString.substringAfter("(")
    val membersMap: Map<String, String> = if (members == fqn) {
      emptyMap()
    } else {
      val membersString = members.removeSuffix(")").trimToNull()
      membersString?.split(",")?.map { it.trim() }?.associate {
        val pair = if (it.contains("=")) it else "value=$it"

        val (key, value) = pair.substringBefore("=") to pair.substringAfter("=")
        key.trim() to value.trim()
      } ?: emptyMap()
    }

    CanonicalName(fqn) to membersMap
  }.get())

  val canonicalName: CanonicalName get() = value.first
  val members: Map<String, String> get() = value.second

  val membersString: String get() = members.entries.joinToString(separator = ",") { "${it.key}=${it.value}" }

  override val key: String get() = PROPERTY_KEY
  override fun toString(): String = "JavaAnnotationProperty(value='$canonicalName($membersString)')"


}
