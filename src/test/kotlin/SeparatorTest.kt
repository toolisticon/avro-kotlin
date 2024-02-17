package io.toolisticon.avro.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class SeparatorTest {

  @Test
  fun `replace dots and dashes`() {
    val dots = "io.acme.bar.Foo"
    val dashes = AvroKotlin.Separator.dotToDash(dots)

    assertThat(dashes).isEqualTo("io/acme/bar/Foo")
    assertThat(AvroKotlin.Separator.dashToDot(dashes)).isEqualTo(dots)
  }
}
