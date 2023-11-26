package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.ktx.dotToDash
import java.nio.file.Path
import kotlin.io.path.Path

@JvmInline
value class Namespace(override val value: String) : ValueType<String> {

  operator fun plus(name: Name) = CanonicalName(this to name)

  /**
   * [Namespace] to [Path] replacing `.` with [File#separator].
   */
  fun toPath(): Path = Path(value.dotToDash())


  override fun toString() = value
}
