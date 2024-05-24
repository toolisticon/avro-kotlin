package io.toolisticon.avro.kotlin.value

import _ktx.StringKtx
import io.toolisticon.avro.kotlin.AvroKotlin.Separator
import io.toolisticon.avro.kotlin._ktx.KotlinKtx.create
import io.toolisticon.avro.kotlin.value.Name.Companion.toName
import io.toolisticon.avro.kotlin.value.Namespace.Companion.toNamespace
import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Combining namespace and name as a [java.lang.Class#canonicalName].
 */
@JvmInline
value class CanonicalName private constructor(override val value: Pair<Namespace, Name>) : PairType<Namespace, Name> {
  companion object {
    val EMPTY = Namespace.EMPTY + Name.EMPTY

    fun parse(fqn: String) = CanonicalName(value = create {
      if (fqn.contains(Separator.NAME)) {
        val (namespace, name) = fqn.substringBeforeLast(Separator.NAME) to fqn.substringAfterLast(Separator.NAME)
        Namespace(namespace) to Name(name)
      } else {
        Namespace.EMPTY to Name(fqn)
      }
    })

    fun Pair<String, String>.toCanonicalName(): CanonicalName = CanonicalName(
      namespace = this.first.toNamespace(),
      name = this.second.toName()
    )

    fun String.toCanonicalName(): CanonicalName = CanonicalName.parse(this)
  }

  constructor(namespace: Namespace, name: Name) : this(namespace to name)

  val namespace: Namespace get() = value.first
  val name: Name get() = value.second

  val fqn: String get() = "${namespace.value}${if (namespace.isEmpty()) "" else Separator.NAME}${name.value}"

  /**
   * Turns a canonical (fqn) name to a file system [Path] using suffix.
   *
   * A java class `io.acme.Foo` would become `io/acme/Foo.java`.
   */
  fun toPath(fileExtension: String): Path = namespace.toPath().resolve(name.toPath(fileExtension)).normalize()

  fun toPath(specification: AvroSpecification): Path = toPath(specification.value)

  override fun toString() = StringKtx.toString("CanonicalName") {
    add(property = "fqn", value = fqn, wrap = "'")
  }
}
