package io.toolisticon.avro.kotlin.value

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test


internal class ByteArrayValueTest {

  private val helloBytes: ByteArray = "Hello World!".encodeToByteArray()
  private val helloHex: String = "[48 65 6C 6C 6F 20 57 6F 72 6C 64 21]"

  @Test
  fun `byte array from to hex string`() {
    val bytes = ByteArrayValue(helloHex)

    assertThat(bytes.formatted).isEqualTo(helloHex)
  }

  @Test
  fun `from to buffer`() {
    val bytes = ByteArrayValue(helloBytes)
    val buffer = bytes.buffer
    assertThat(ByteArrayValue(buffer)).isEqualTo(bytes)
  }

  @Test
  fun `split byteArray`() {
    val bytes = ByteArrayValue(helloBytes)
    assertThatThrownBy { bytes.split(-1) }.hasMessage("all indexes have to match '0 < index < size-1', was: indexes=[-1], size=12")
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThatThrownBy { bytes.split(100) }.hasMessage("all indexes have to match '0 < index < size-1', was: indexes=[100], size=12")
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThatThrownBy { bytes.split(10, 5) }.hasMessage("indexes must be ordered, was: [10, 5]")
      .isInstanceOf(IllegalArgumentException::class.java)
    assertThatThrownBy { bytes.split(10, 10) }.hasMessage("indexes must be unique, was: [10, 10]")
      .isInstanceOf(IllegalArgumentException::class.java)

    val parts = bytes.split(5, 10)

    assertThat(parts).hasSize(3)
    assertThat(parts[0].formatted).isEqualTo("[48 65 6C 6C 6F]")
    assertThat(parts[1].formatted).isEqualTo("[20 57 6F 72 6C]")
    assertThat(parts[2].formatted).isEqualTo("[64 21]")
  }


  @Test
  fun `extract from bytes`() {
    val bytes = ByteArrayValue(helloBytes)
    val extracted = bytes.extract(8, 3)

    assertThat(extracted.formatted).isEqualTo("[72 6C 64]")
  }
}
