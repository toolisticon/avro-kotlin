package io.toolisticon.kotlin.avro

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

@Nested
class SeparatorTest {

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `replace dots and dashes`() {
    val dots = "io.acme.bar.Foo"
    val dashes = AvroKotlin.Separator.dotToDash(dots)

    assertThat(dashes).isEqualTo("io/acme/bar/Foo")
    assertThat(AvroKotlin.Separator.dashToDot(dashes)).isEqualTo(dots)
  }
}
