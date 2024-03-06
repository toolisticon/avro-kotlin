package io.toolisticon.avro.kotlin.model.wrapper

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.model.EmptyType
import io.toolisticon.avro.kotlin.value.Name
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroProtocolTest {

  @Test
  fun `parse simple_avpr - types contain empty message request`() {
    val protocol: AvroProtocol = AvroKotlin.parseProtocol("org.apache.avro/protocol/simple.avpr")

    val ackMessage = requireNotNull(protocol.getMessage(Name("ack")))
    assertThat(ackMessage).isInstanceOf(AvroProtocol.OneWayMessage::class.java)
    assertThat(ackMessage.request).isEqualTo(EmptyType.schema)

    val types = protocol.types

    assertThat(types).isNotEmpty
  }
}
