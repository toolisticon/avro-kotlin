package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.AvroSpecification.SCHEMA
import io.toolisticon.avro.kotlin.value.Name
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
