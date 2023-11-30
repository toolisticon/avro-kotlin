package io.toolisticon.avro.kotlin.name

@JvmInline
value class CanonicalName(private val value: Pair<Namespace, Name>) {

  val namespace: Namespace get() = value.first
  val name: Name get() = value.second
  override fun toString() = "CanonicalName(value=$value)"

}
