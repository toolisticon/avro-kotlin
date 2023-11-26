package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.ktx.trimToNull
import io.toolisticon.avro.kotlin.ktx.withNamespace

@JvmInline
value class Name(override val value: String) : ValueType<String> {
  fun withNamespace(namespace: Namespace?) = value.withNamespace(namespace?.value)

  fun suffix(suffix: String?): Name = Name("$value${suffix.trimToNull()?.replaceFirstChar(Char::uppercase) ?: ""}")

  override fun toString() = value
}
