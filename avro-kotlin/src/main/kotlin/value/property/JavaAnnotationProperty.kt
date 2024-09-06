package io.toolisticon.kotlin.avro.value.property

import _ktx.StringKtx.trimToNull
import io.toolisticon.kotlin.avro._ktx.KotlinKtx.create
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.avro.value.ObjectProperties
import io.toolisticon.kotlin.avro.value.ValueType

/**
 * A string representing a java annotation as read from additional [ObjectProperties].
 */
@JvmInline
value class JavaAnnotationProperty private constructor(override val value: Pair<CanonicalName, Map<String, String>>) :
  ValueType<Pair<CanonicalName, Map<String, String>>>, AvroProperty {

  companion object : AvroPropertySupplier<List<JavaAnnotationProperty>> {
    const val PROPERTY_KEY = "javaAnnotation"

    override fun from(properties: ObjectProperties): List<JavaAnnotationProperty> = when (val annotationsValue = properties[PROPERTY_KEY]) {
      is String -> listOf(JavaAnnotationProperty(annotationsValue))
      is List<*> -> annotationsValue.filterIsInstance<String>().map { JavaAnnotationProperty(it) }
      else -> emptyList()
    }
  }

  constructor(annotationString: String) : this(value = create {
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

    CanonicalName.parse(fqn) to membersMap
  })

  val canonicalName: CanonicalName get() = value.first
  val members: Map<String, String> get() = value.second

  val membersString: String get() = members.entries.joinToString(separator = ", ") { "${it.key}=${it.value}" }

  override val key: String get() = PROPERTY_KEY
  override fun toString(): String = "JavaAnnotationProperty('@${canonicalName.fqn}($membersString)')"


}
