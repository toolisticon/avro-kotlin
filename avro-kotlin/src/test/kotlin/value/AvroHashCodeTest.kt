package io.toolisticon.avro.kotlin.value

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class AvroHashCodeTest {

  @Test
  fun `can create hashCode from HexString`() {
    val intValue = Random.nextInt()

    val hashCode = AvroHashCode(intValue)
    val string = hashCode.toString()

    assertThat(AvroHashCode.of(hashCode.hex)).isEqualTo(hashCode)
    assertThat(AvroHashCode.of(string)).isEqualTo(hashCode)
  }
}
