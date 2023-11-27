package io.toolisticon.avro.kotlin.ext

import io.toolisticon.avro.kotlin.ktx.dashToDot
import io.toolisticon.avro.kotlin.ktx.dotToDash
import io.toolisticon.avro.kotlin.ktx.file
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.Path

internal class IoExtTest {

  @TempDir
  lateinit var tmp: File



  @Test
  fun `resolve path under dir`() {
    val path = Path("foo/bar/File.txt")

    val result = tmp.file(path)

    assertThat(result.toString()).startsWith(tmp.toString())
    assertThat(result.toString()).endsWith(path.toString())
  }


  @Test
  fun `replace dots and dashes`() {
    val dots = "io.acme.bar.Foo"
    val dashes = dots.dotToDash()

    assertThat(dashes).isEqualTo("io/acme/bar/Foo")
    assertThat(dashes.dashToDot()).isEqualTo(dots)
  }


  @Test
  fun `extract subpath`() {
    val root = Path("/foo/bar/ggg")
    val full = Path("/foo/bar/com/acme/baz/hello.world")

    val sub = root.relativize(full)

    assertThat(sub.toString()).isEqualTo("../com/acme/baz/hello.world")
  }
}
