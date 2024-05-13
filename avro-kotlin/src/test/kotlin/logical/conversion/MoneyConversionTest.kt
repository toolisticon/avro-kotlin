package io.toolisticon.avro.kotlin.logical.conversion

import io.toolisticon.avro.kotlin._test.MoneyConversion
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test

internal class MoneyConversionTest {
  val conversion = MoneyConversion()

  @Test
  fun `convert via format and parse`() {
    val money = Money.of(200.01, "EUR")

    val string = conversion.toAvro(money)

    assertThat(conversion.fromAvro(string)).isEqualTo(money)
  }
}
