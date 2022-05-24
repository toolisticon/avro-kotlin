package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.AvroKotlinLib
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroFqnTest {

  @Test
  fun `canonical name of fqn`() {
    val fqn = AvroKotlinLib.fqn("com.acme.test", "HelloWorld")

    assertThat(fqn.canonicalName).isEqualTo("com.acme.test.HelloWorld")
  }
}
