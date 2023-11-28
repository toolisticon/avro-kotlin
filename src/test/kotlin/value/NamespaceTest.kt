package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroBuilder
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NamespaceTest {

  @Test
  fun `namespace to path`() {
    val ns = Namespace("test.lib")
    assertThat(ns.toPath().toString()).isEqualTo("test/lib")
  }

  @Test
  fun `EMPTY ns is empty path`() {
    assertThat(Namespace.EMPTY.toPath()).hasToString("")
  }

  @Test
  fun `primitive type has EMPTY namespace`() {
    assertThat(Namespace(AvroBuilder.primitiveSchema(Schema.Type.STRING).schema)).isEqualTo(Namespace.EMPTY)
  }
}
