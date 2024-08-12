package io.toolisticon.kotlin.avro.example.money

import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test

internal class MoneyLogicalTypeTest {

  @Test
  fun `convert money`() {
    val money = Money.of(10, "EUR")
    val string = MoneyLogicalType.converter.toAvro(money)

    assertThat(MoneyLogicalType.converter.fromAvro(string)).isEqualTo(money)
  }

}
