package io.toolisticon.kotlin.avro.logical.conversion

import io.toolisticon.kotlin.avro._test.MoneyConversionParameterized
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test

internal class MoneyConversionTest {
  val conversion = MoneyConversionParameterized()

  @Test
  fun `convert via format and parse`() {
    val money = Money.of(200.01, "EUR")

    val string = conversion.toAvro(money)

    assertThat(conversion.fromAvro(string)).isEqualTo(money)
  }
}
