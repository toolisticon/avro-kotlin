package io.toolisticon.avro.kotlin.ktx

import io.toolisticon.avro.kotlin.AvroKotlin.StringKtx.trimToNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class StringKtxTest {

  @Test
  fun `trim to null`() {
    assertThat(null.trimToNull()).isEqualTo(null)
    assertThat("".trimToNull()).isEqualTo(null)
    assertThat(" ".trimToNull()).isEqualTo(null)
    assertThat(" h ".trimToNull()).isEqualTo(" h ")
  }
}
