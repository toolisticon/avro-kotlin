package io.toolisticon.avro.kotlin.ext

import io.toolisticon.avro.kotlin._bak.ProtocolFqn.Companion.protocolFqn
import io.toolisticon.avro.kotlin._bak.fromResource
import io.toolisticon.avro.kotlin._test.TestFixtures
import io.toolisticon.avro.kotlin.ktx.message
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
