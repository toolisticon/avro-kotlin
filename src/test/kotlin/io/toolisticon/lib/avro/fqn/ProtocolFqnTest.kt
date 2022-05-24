package io.toolisticon.lib.avro.fqn

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ProtocolFqnTest {

  @Test
  fun `toString contains namespace name and extension`() {
    val fqn = ProtocolFqn(namespace = "com.acme.foo", name = "MyProtocol")

    assertThat(fqn.toString()).isEqualTo("ProtocolFqn(namespace='com.acme.foo', name='MyProtocol', extension='avpr')")
  }
}
