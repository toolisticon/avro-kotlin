package io.toolisticon.lib.avro.fqn

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SchemaFqnTest {

  @Test
  fun `toString contains namespace name and extension`() {
    val fqn = SchemaFqn(namespace = "com.acme.foo", name = "MySchema")

    assertThat(fqn.toString()).isEqualTo("SchemaFqn(namespace='com.acme.foo', name='MySchema', extension='avsc')")
  }
}
