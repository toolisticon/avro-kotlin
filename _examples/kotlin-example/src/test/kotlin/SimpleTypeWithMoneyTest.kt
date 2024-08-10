package io.toolisticon.kotlin.avro.example

import io.toolisticon.kotlin.avro.example.KotlinExample.avro
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test


internal class SimpleTypeWithMoneyTest {

  @Test
  fun `serialize single object with money type`() {
    val orig = SimpleTypeWithMoney(Money.of(10, "EUR"))
    println(avro.schema(SimpleTypeWithMoney::class))

    val encoded = avro.encodeToSingleObjectEncoded(orig)
    println(encoded)

    val decoded = avro.decodeFromSingleObjectEncoded<SimpleTypeWithMoney>(encoded)

    assertThat(decoded).isEqualTo(orig)
  }
}
