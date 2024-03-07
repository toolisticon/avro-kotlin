package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin.Separator
import java.nio.file.Path
import java.util.function.Supplier
import kotlin.io.path.Path

/**
 * Combining namespace and name as a [java.lang.Class#canonicalName].
 */
@JvmInline
value class CanonicalName(override val value: Pair<Namespace, Name>) : ValueType<Pair<Namespace, Name>> {
  companion object {
    val EMPTY = Namespace.EMPTY + Name.EMPTY
  }

  constructor(namespace: Namespace, name: Name) : this(namespace to name)
  constructor(fqn: String) : this(value = Supplier<Pair<Namespace, Name>> {
    if (fqn.contains(Separator.NAME)) {
      val (namespace, name) = fqn.substringBeforeLast(Separator.NAME) to fqn.substringAfterLast(Separator.NAME)
      Namespace(namespace) to Name(name)
    } else {
      Namespace.EMPTY to Name(fqn)
    }
  }.get())

  val namespace: Namespace get() = value.first
  val name: Name get() = value.second

  /**
   * Turns a canonical (fqn) name to a file system [Path] using suffix.
   *
   * A java class `io.acme.Foo` would become `io/acme/Foo.java`.
   */
  fun toPath(fileExtension: String): Path = namespace.toPath().resolve(name.toPath(fileExtension)).normalize()

  fun toPath(specification: AvroSpecification): Path = toPath(specification.value)

  override fun toString() = "${namespace.value}${if (namespace.isEmpty()) "" else Separator.NAME}${name.value}"
}
