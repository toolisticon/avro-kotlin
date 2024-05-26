package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.builder.AvroBuilder
import io.toolisticon.kotlin.avro.model.SchemaType.STRING
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

@DisabledOnOs(OS.WINDOWS)
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
    assertThat(AvroBuilder.primitiveSchema(STRING).namespace).isEqualTo(Namespace.EMPTY)
  }
}
