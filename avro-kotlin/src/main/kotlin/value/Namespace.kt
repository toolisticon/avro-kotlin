package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin.Separator.dotToDash
import org.apache.avro.Protocol
import org.apache.avro.Schema
import java.nio.file.Path
import kotlin.io.path.Path

/**
 *
 *
 * TODO: check support functions of org.apache.avro.Schema.Name
 */
@JvmInline
value class Namespace(override val value: String) : ValueType<String> {
  companion object {
    val EMPTY = Namespace("")

    fun String.toNamespace(): Namespace = Namespace(this)

    fun of(schema: Schema): Namespace = runCatching { Namespace(schema.namespace) }.getOrDefault(EMPTY)
    fun of(protocol: Protocol) = Namespace(
      value = requireNotNull(protocol.namespace) { "protocol must have a namespace" }
    )
  }

  operator fun plus(name: Name) = CanonicalName(this, name)

  /**
   * [Namespace] to [Path] replacing `.` with [File#separator].
   */
  fun toPath(): Path = Path(dotToDash(value))

  fun isEmpty(): Boolean = EMPTY == this

  override fun toString() = value
}
