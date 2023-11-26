package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.AvroKotlin.Constants.AVRO_V1_HEADER
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class HexStringTest {

  @Test
  fun `hex of 1 is 01`() {
    val hex = HexString(1)

    assertThat(hex.value).isEqualTo("01")
    assertThat(hex.toString()).isEqualTo("01")
    assertThat(hex.formatted).isEqualTo("[01]")
  }

  @Test
  fun `parse formatted`() {
    val hex = HexString("[C3 01]")
    assertThat(hex.value).isEqualTo("C301")
  }

  @Test
  fun `avro header bytes is C301`() {
    val hex = HexString(AVRO_V1_HEADER)

    assertThat(hex.value).isEqualTo("C301")
    assertThat(hex.toString()).isEqualTo("C301")
    assertThat(hex.formatted).isEqualTo("[C3 01]")
  }

  @Test
  fun `fingerprint hex is 8 bytes long`() {
    val schema = primitiveSchema(Schema.Type.STRING)
    val hex = HexString(schema.fingerprint)

    assertThat(hex.length).isEqualTo(16)
  }
}
