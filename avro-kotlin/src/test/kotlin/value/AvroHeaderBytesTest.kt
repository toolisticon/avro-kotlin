package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro._test.FooString
import org.apache.avro.message.BadHeaderException
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test


internal class AvroHeaderBytesTest {

  @Test
  fun `header bytes are C301`() {
    assertThat(SingleObjectEncodedBytes.AVRO_V1_HEADER.hex.formatted).isEqualTo("[C3 01]")
  }

  @Test
  fun `require header bytes`() {
    // no exception
    assertThatNoException().isThrownBy {
      val validBytes = FooString.SINGLE_OBJECT_BAR.byteArray
      AvroHeaderBytes.verify(validBytes)
    }

    // bad header exception
    assertThatThrownBy {
      val invalidBytes = ByteArrayValue("Hello World!".encodeToByteArray())
      AvroHeaderBytes.verify(invalidBytes)
    }.isInstanceOf(BadHeaderException::class.java)
      .hasMessage("Unrecognized header bytes: [0x48 0x65]")
  }
}
