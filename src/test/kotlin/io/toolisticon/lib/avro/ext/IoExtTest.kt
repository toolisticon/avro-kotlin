package io.toolisticon.lib.avro.ext

import io.toolisticon.lib.avro.AvroKotlinLib
import io.toolisticon.lib.avro.AvroKotlinLib.EXTENSION_SCHEMA
import io.toolisticon.lib.avro.ext.IoExt.dashToDot
import io.toolisticon.lib.avro.ext.IoExt.dotToDash
import io.toolisticon.lib.avro.ext.IoExt.file
import io.toolisticon.lib.avro.ext.IoExt.fqnToPath
import io.toolisticon.lib.avro.ext.IoExt.namespaceToPath
import io.toolisticon.lib.avro.ext.ResourceExt.resource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.Path

internal class IoExtTest {
  @TempDir
  lateinit var tmp: File


  @Test
  fun `namespace to path`() {
    assertThat(namespaceToPath("test.lib").toString()).isEqualTo("test/lib")
  }

  @Test
  fun `fqn to path`() {
    assertThat(fqnToPath(namespace = "com.acme", name = "Foo", fileExtension = EXTENSION_SCHEMA).toString())
      .isEqualTo("com/acme/Foo.avsc")
  }

  @Test
  fun `resolve path under dir`() {
    val path = Path("foo/bar/File.txt")

    val result = tmp.file(path)

    assertThat(result.toString()).startsWith(tmp.toString())
    assertThat(result.toString()).endsWith(path.toString())
  }

  @Test
  fun `get resource url`() {
    val url = resource("lib.test.event", "BankAccountCreated", EXTENSION_SCHEMA, "avro")

    println(AvroKotlinLib.DEFAULT_CLASS_LOADER.getResource(""))

    println(url)
  }

  @Test
  fun `replace dots and dashes`() {
    val dots = "io.acme.bar.Foo"
    val dashes = dots.dotToDash()

    assertThat(dashes).isEqualTo("io/acme/bar/Foo")
    assertThat(dashes.dashToDot()).isEqualTo(dots)
  }
}
