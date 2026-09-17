package io.toolisticon.kotlin.avro.example

import io.toolisticon.kotlin.avro.example.KotlinExample.avro
import org.apache.avro.util.ClassSecurityValidator
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SimpleTypeWithMoneyTest {

  private lateinit var previousClassSecurityValidator: ClassSecurityValidator.ClassSecurityPredicate

  @BeforeAll
  fun trustBankAccountCreated() {
    previousClassSecurityValidator = ClassSecurityValidator.getGlobal()
    ClassSecurityValidator.setGlobal(
      ClassSecurityValidator.composite(
        ClassSecurityValidator.DEFAULT_TRUSTED_CLASSES,
        ClassSecurityValidator.builder()
          .add(SimpleTypeWithMoney::class.java)
          .build()
      )
    )
  }

  @AfterAll
  fun restoreClassSecurityValidator() {
    ClassSecurityValidator.setGlobal(previousClassSecurityValidator)
  }

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
