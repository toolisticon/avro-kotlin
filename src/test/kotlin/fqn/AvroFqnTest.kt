package io.toolisticon.avro.kotlin.fqn

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroFqnTest {

  @Test
  fun `canonical name of fqn`() {
    val fqn = AvroKotlin.fqn(Namespace("com.acme.test"), Name("HelloWorld"))

    assertThat(fqn.canonicalName).isEqualTo("com.acme.test.HelloWorld")
  }
}
