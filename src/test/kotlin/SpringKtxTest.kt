package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.trimToNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@Nested
class SpringKtxTest {
  @ParameterizedTest
  @CsvSource(
    value = [
      "null,null",
      ",null",
      " ,null",
      " h ,h",
    ], nullValues = ["null"]
  )
  fun `String trimToNull`(input: String?, expected: String?) {
    assertThat(input.trimToNull()).isEqualTo(expected)
  }

  @Test
  fun `csv of non null strings`() {
    assertThat(AvroKotlin.StringKtx.csv()).isEqualTo("")
    assertThat(AvroKotlin.StringKtx.csv(null)).isEqualTo("")
    assertThat(AvroKotlin.StringKtx.csv("1")).isEqualTo("1")
  }
}
