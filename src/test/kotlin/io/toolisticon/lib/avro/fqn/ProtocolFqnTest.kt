package io.toolisticon.lib.avro.fqn

import io.toolisticon.avro.kotlin._bak.ProtocolFqn
import io.toolisticon.avro.kotlin.name.Name
import io.toolisticon.avro.kotlin.name.Namespace
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ProtocolFqnTest {

  @Test
  fun `toString contains namespace name and extension`() {
    val fqn = ProtocolFqn(namespace = Namespace("com.acme.foo"), name = Name("MyProtocol"))

    assertThat(fqn.toString()).isEqualTo("ProtocolFqn(namespace='com.acme.foo', name='MyProtocol', fileExtension='avpr')")
  }
}
