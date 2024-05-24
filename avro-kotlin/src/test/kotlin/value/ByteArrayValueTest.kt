package io.toolisticon.avro.kotlin.value

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class ByteArrayValueTest {

  private val helloBytes: ByteArray = "Hello World!".encodeToByteArray()
  private val helloHex: String = "[48 65 6C 6C 6F 20 57 6F 72 6C 64 21]"

  @Test
  fun `byte array from to hex string`() {
    val bytes = ByteArrayValue.parse(helloHex)

    assertThat(bytes.formatted).isEqualTo(helloHex)
  }

  @Test
  fun `from to buffer`() {
    val bytes = ByteArrayValue(helloBytes)
    val buffer = bytes.buffer
    assertThat(ByteArrayValue(buffer)).isEqualTo(bytes)
  }

}
