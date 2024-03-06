package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.TestFixtures
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.Path

internal class DirectoryTest {

  @TempDir
  private lateinit var tmp: Path

  private val dir by lazy { Directory(tmp) }

  @Test
  fun `verify toString`() {
    assertThat(dir.toString())
      .startsWith("Directory('/")
      .endsWith("')")
  }


  @Test
  fun `resolve path under dir`() {
    val path = Path("foo/bar/File.txt")
    val result = dir.resolve(path)

    assertThat(result.toString()).startsWith(tmp.toString())
    assertThat(result.toString()).endsWith(path.toString())
  }

  @Test
  fun `write json to dir`() {
    val schema = TestFixtures.RECORD_MAP_WITH_NULLABLE_UUIDS

    dir.write(
      fqn = schema.canonicalName,
      type = AvroSpecification.SCHEMA,
      content = schema.json
    )

    assertThat(dir.walk().map {
      it.toString()
    }.toList()).anyMatch {
      it.endsWith("/xxx/yyy/Foo.avsc")
    }

    val writtenSchema = dir.findSchemaFiles().first()

    assertThat(writtenSchema.toString()).endsWith("/xxx/yyy/Foo.avsc")
  }

  @Test
  fun `find all schemaDeclarations`() {
    assertThat(dir.findSchemaFiles().toList()).isEmpty()

    val schemaFoo = TestFixtures.schemaFoo
    val schemaBar = TestFixtures.schemaBar

    dir.write(schemaFoo)
    dir.write(schemaBar)
    assertThat(dir.findSchemaFiles().toList()).hasSize(2)
  }


  @Test
  fun `extract subpath`() {

    val root = dir.mkDir("ggg")
    val full = dir.mkDir("com").resolve("acme/baz/hello.world")

    val sub = root.relativize(full)

    assertThat(sub.toString()).isEqualTo("../com/acme/baz/hello.world")
  }

}
