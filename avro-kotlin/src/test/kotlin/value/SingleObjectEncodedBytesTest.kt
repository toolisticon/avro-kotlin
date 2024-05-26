package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro._test.FooString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SingleObjectEncodedBytesTest {

  @Test
  fun `can construct from hex String`() {
    val soe = SingleObjectEncodedBytes.parse(FooString.SINGLE_OBJECT_BAR)

    assertThat(soe.hex).isEqualTo(FooString.SINGLE_OBJECT_BAR)
    assertThat(soe.payload.hex.formatted).isEqualTo("[06 62 61 72]")
    assertThat(soe.fingerprint.hex.formatted).isEqualTo("[68 2F CD 81 41 03 69 8A]")
  }

  @Test
  fun `encode fooString and derive fingerprint from bytes`() {
    val record = FooString("bar").toGenericRecord()
    val encoded = AvroKotlin.genericRecordToSingleObjectEncoded(record)

    assertThat(encoded.fingerprint).isEqualTo(FooString.SCHEMA.fingerprint)
  }
}
