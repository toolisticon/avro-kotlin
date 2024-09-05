package io.holixon.axon.avro.generator

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.junit.jupiter.api.Test

internal class NamingTest {
  private val declaration = TestFixtures.parseProtocol("BankAccountProtocol.avpr")

  @Test
  fun `convert protocol name to lower-kebab`() {
    val name = declaration.name.value
    val kebab = PropertyNamingStrategies.KebabCaseStrategy().translate(name);

  }
}
