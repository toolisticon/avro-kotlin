package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.AvroKotlin.AVRO_V1_HEADER
import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.STRING
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import kotlin.random.Random


internal class HexStringTest {

  @Test
  fun `hex of 1 is 01`() {
    val hex = HexString.of(1)

    assertThat(hex.value).isEqualTo("00000001")
    assertThat(hex.toString()).isEqualTo("00000001")
    assertThat(hex.formatted).isEqualTo("[00 00 00 01]")
  }

  @Test
  fun `parse formatted`() {
    val hex = HexString.parse("[C3 01]")
    assertThat(hex.value).isEqualTo("C301")
  }

  @Test
  fun `avro header bytes is C301`() {
    val hex = AVRO_V1_HEADER.hex

    assertThat(hex.value).isEqualTo("C301")
    assertThat(hex.toString()).isEqualTo("C301")
    assertThat(hex.formatted).isEqualTo("[C3 01]")
  }


  @Test
  fun `fingerprint hex is 8 bytes long`() {
    val schema = primitiveSchema(STRING)
    val hex = schema.fingerprint.hex

    assertThat(hex.length).isEqualTo(16)
  }

  @Test
  fun `create from int and convert back into int`() {
    SoftAssertions().apply {
      repeat(100) {
        val randomInt = Random.nextInt()

        val hex = HexString.of(randomInt)

        assertThat(hex.parseInt()).`as` {
          "expected $hex to be parsed as $randomInt"
        }.isEqualTo(randomInt)

      }
    }.assertAll()
  }
}
