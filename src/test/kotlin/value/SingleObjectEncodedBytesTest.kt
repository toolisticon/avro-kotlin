package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.TestFixtures.SINGLE_STRING_ENCODED
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SingleObjectEncodedBytesTest {

  @Test
  fun `from hex String`() {
    val soe = SingleObjectEncodedBytes(ByteArrayValue(SINGLE_STRING_ENCODED).value)

    assertThat(soe.hex.formatted).isEqualTo(SINGLE_STRING_ENCODED)
    assertThat(soe.fingerprint.value).isEqualTo(4162171688006255043L)
    assertThat(soe.payload.formatted).isEqualTo("[06 62 61 72]")
  }

}
