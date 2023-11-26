package io.toolisticon.avro.kotlin.value

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NamespaceTest {

  @Test
  fun `namespace to path`() {
    val ns = Namespace("test.lib")
    assertThat(ns.toPath().toString()).isEqualTo("test/lib")
  }
}
