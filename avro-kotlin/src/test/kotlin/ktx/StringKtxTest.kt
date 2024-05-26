package io.toolisticon.kotlin.avro.ktx

import _ktx.StringKtx
import _ktx.StringKtx.trimToNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class StringKtxTest {

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
    assertThat(StringKtx.csv()).isEqualTo("")
    assertThat(StringKtx.csv(null)).isEqualTo("")
    assertThat(StringKtx.csv("1")).isEqualTo("1")
  }
}
