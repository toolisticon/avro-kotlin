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

  @Test
  fun `verify toString`() {
    assertThat(Directory(tmp).toString())
      .startsWith("Directory(/")
      .endsWith(")")
  }

  @Test
  fun walk() {
    Directory(tmp).walk().forEach {
      println(it)
    }
  }

  @Test
  fun `resolve path under dir`() {
    val path = Path("foo/bar/File.txt")
    val result = Directory(tmp).resolve(path)

    assertThat(result.toString()).startsWith(tmp.toString())
    assertThat(result.toString()).endsWith(path.toString())
  }

  @Test
  fun `write json to dir`() {
    val dir = Directory(tmp)

    val schema = TestFixtures.RECORD_MAP_WITH_NULLABLE_UUIDS

    val file = dir.write(
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
}
