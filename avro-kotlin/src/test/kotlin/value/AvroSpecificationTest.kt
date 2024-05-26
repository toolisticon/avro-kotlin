package io.toolisticon.kotlin.avro.value

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class AvroSpecificationTest {

  @Test
  fun `find declaration by extension`() {
    assertThat(AvroSpecification.valueOfExtension("avsc")).isEqualTo(AvroSpecification.SCHEMA)
    assertThat(AvroSpecification.valueOfExtension("avpr")).isEqualTo(AvroSpecification.PROTOCOL)
  }

  @Test
  fun `unsupported extension`() {
    assertThatThrownBy { AvroSpecification.valueOfExtension("foo") }
      .isInstanceOf(java.lang.IllegalArgumentException::class.java)
      .hasMessage("Not a valid extension='foo', must be one of: [avsc, avpr].")
  }
}
