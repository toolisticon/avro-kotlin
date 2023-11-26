package io.toolisticon.avro.kotlin.value

@JvmInline
value class CanonicalName(override val value: Pair<Namespace, Name>) : ValueType<Pair<Namespace, Name>> {

  val namespace: Namespace get() = value.first
  val name: Name get() = value.second
  override fun toString() = "CanonicalName(value=$value)"

}
