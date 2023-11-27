package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin.Constants.FILE_SEPARATOR
import io.toolisticon.avro.kotlin.AvroKotlin.Constants.NAME_SEPARATOR
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
  fun toPath(fileExtension: String): Path = Path(namespace.toPath().toString() + FILE_SEPARATOR + name.value + NAME_SEPARATOR + fileExtension).normalize()

  fun toPath(specification: AvroSpecification): Path = toPath(specification.value)

  override fun toString() = "${namespace.value}$NAME_SEPARATOR${name.value}"
}
