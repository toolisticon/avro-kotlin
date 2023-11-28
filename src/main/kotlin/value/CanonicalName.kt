package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin.Separator.FILE
import io.toolisticon.avro.kotlin.AvroKotlin.Separator.NAME
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Combining namespace and name as a [java.lang.Class#canonicalName].
 */
@JvmInline
value class CanonicalName(override val value: Pair<Namespace, Name>) : ValueType<Pair<Namespace, Name>> {

  constructor(namespace: Namespace, name: Name) : this(namespace to name)

  val namespace: Namespace get() = value.first
  val name: Name get() = value.second

  /**
   * Turns a canonical (fqn) name to a file system [Path] using suffix.
   *
   * A java class `io.acme.Foo` would become `io/acme/Foo.java`.
   */
  fun toPath(fileExtension: String): Path = namespace.toPath().resolve(name.toPath(fileExtension)).normalize()

  fun toPath(specification: AvroSpecification): Path = toPath(specification.value)

  override fun toString() = "${namespace.value}${if (namespace.isEmpty()) "" else NAME}${name.value}"
}
