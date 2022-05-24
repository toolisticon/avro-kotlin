package io.toolisticon.lib.avro.io

import io.toolisticon.lib.avro.AvroKotlinLib.EXTENSION_SCHEMA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.Path

internal class FileExtensionsTest {

  @TempDir
  lateinit var tmp:File


  @Test
  fun `namespace to path`() {
    assertThat(namespaceToPath("test.lib").toString()).isEqualTo("test/lib")
  }

  @Test
  fun `fqn to path`() {
    assertThat(fqnToPath(namespace = "com.acme", name="Foo", fileExtension = EXTENSION_SCHEMA).toString())
      .isEqualTo("com/acme/Foo.avsc")
  }

  @Test
  fun `resolve path under dir`() {
    val path = Path("foo/bar/File.txt")

    println(tmp)

    val result = tmp.file(path)

    assertThat(result.toString()).startsWith(tmp.toString())
    assertThat(result.toString()).endsWith(path.toString())

  }
}
