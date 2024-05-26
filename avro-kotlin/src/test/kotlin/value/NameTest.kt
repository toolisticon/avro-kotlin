package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.value.AvroSpecification.SCHEMA
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NameTest {

  @Test
  fun `add suffix to name`() {
    val name = Name("Foo")

    assertThat(name.suffix("Bar")).isEqualTo(Name("FooBar"))
  }

  @Test
  fun `path with fileExtension`() {
    assertThat(Name("Foo").toPath(SCHEMA)).hasToString("Foo.avsc")
  }
}
