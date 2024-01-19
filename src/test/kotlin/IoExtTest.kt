package io.toolisticon.avro.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.Path

internal class IoExtTest {

  @TempDir
  lateinit var tmp: File


  @Test
  fun `extract subpath`() {
    val root = Path("/foo/bar/ggg")
    val full = Path("/foo/bar/com/acme/baz/hello.world")

    val sub = root.relativize(full)

    assertThat(sub.toString()).isEqualTo("../com/acme/baz/hello.world")
  }
}
