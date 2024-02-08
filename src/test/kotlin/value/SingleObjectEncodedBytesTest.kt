package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin._test.FooString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SingleObjectEncodedBytesTest {

  @Test
  fun `can construct from hex String`() {
    val soe = SingleObjectEncodedBytes(FooString.SINGLE_OBJECT_BAR)
    println(FooString.SINGLE_OBJECT_BAR.formatted)
    println("--")
    println(soe.fingerprint.hex.formatted)
    println("--")

    assertThat(soe.hex).isEqualTo(FooString.SINGLE_OBJECT_BAR)
    assertThat(soe.payload.hex.formatted).isEqualTo("[06 62 61 72]")
    assertThat(soe.fingerprint.hex.formatted).isEqualTo("[1D 6C 12 78 03 3B 7C A0]")
  }

  @Test
  fun `encode fooString and derive fingerprint from bytes`() {
    val record = FooString("bar").toGenericRecord()
    val encoded = AvroKotlin.genericRecordToSingleObjectEncoded(record)

    assertThat(encoded.fingerprint).isEqualTo(FooString.SCHEMA.fingerprint)
  }
}
