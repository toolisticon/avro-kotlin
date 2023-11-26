package io.toolisticon.avro.kotlin.value

@JvmInline
value class Namespace(override val value: String) : ValueType<String> {

  override fun toString() = value
}
