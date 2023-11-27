package io.toolisticon.avro.kotlin.ext

import io.toolisticon.avro.kotlin.ktx.message
import org.apache.avro.Protocol
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

@Ignore("remove")
@Deprecated("remove")
internal class ProtocolExtTest {

  @Test
  fun `get message from protocol`() {
    val protocol : Protocol = TODO() //fqnFindCurrentBalance.protocolFqn().fromResource("avro")

    assertThat(protocol.message("findCurrentBalanceById").isOneWay).isFalse
  }

  @Test
  fun `fail if message does not exist`() {
    val protocol:Protocol = TODO() //fqnFindCurrentBalance.protocolFqn().fromResource("avro")

    assertThatThrownBy { protocol.message("XXX") }
      .isInstanceOf(IllegalArgumentException::class.java)
  }
}
