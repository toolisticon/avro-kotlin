package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin._test.FooString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SingleObjectEncodedBytesTest {

  @Test
  fun `can read from hex String`() {
    val bytes = ByteArrayValue(FooString.SINGLE_OBJECT_BAR)

    val soe = SingleObjectEncodedBytes(bytes.value)

    assertThat(soe.hex.formatted).isEqualTo(FooString.SINGLE_OBJECT_BAR)
    assertThat(soe.payload.formatted).isEqualTo("[06 62 61 72]")
  }

  @Test
  fun `encode fooString and derive fingerprint from bytes`() {
    val record = FooString.genericRecord(FooString("bar"))
    val encoded = AvroKotlin.genericRecordToSingleObjectEncoded(record)

    assertThat(encoded.fingerprint).isEqualTo(FooString.SCHEMA.fingerprint)
  }
}
