package io.toolisticon.lib.avro.ext

import io.toolisticon.lib.avro.TestFixtures
import io.toolisticon.lib.avro.ext.ProtocolExt.message
import io.toolisticon.lib.avro.fqn.ProtocolFqn.Companion.protocolFqn
import io.toolisticon.lib.avro.fqn.fromResource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class ProtocolExtTest {

  @Test
  fun `get message from protocol`() {
    val protocol = TestFixtures.fqnFindCurrentBalance.protocolFqn().fromResource("avro")

    assertThat(protocol.message("findCurrentBalanceById").isOneWay).isFalse
  }

  @Test
  fun `fail if message does not exist`() {
    val protocol = TestFixtures.fqnFindCurrentBalance.protocolFqn().fromResource("avro")

    assertThatThrownBy { protocol.message("XXX") }
      .isInstanceOf(IllegalArgumentException::class.java)
  }
}
