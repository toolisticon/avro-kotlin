package io.toolisticon.kotlin.avro.model.wrapper

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.model.EmptyType
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.value.Name
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class AvroProtocolTest {

  @Test
  fun `parse simple_avpr - types contain empty one-way message request`() {
    val protocol: AvroProtocol = AvroKotlin.parseProtocol("org.apache.avro/protocol/simple.avpr")

    val ackMessage = requireNotNull(protocol.getMessage(Name("ack")))
    assertThat(ackMessage).isInstanceOf(AvroProtocol.OneWayMessage::class.java)
    assertThat(ackMessage.request).isEqualTo(EmptyType.schema)

    val types = protocol.types

    assertThat(types).isNotEmpty
  }

  @Test
  fun `parse two-way message with empty request`() {
    val protocol = AvroKotlin.parseProtocol("org.apache.avro/protocol/bulk-data.avpr")
    val readMessage = requireNotNull(protocol.getMessage(Name("read"))) as AvroProtocol.TwoWayMessage

    assertAll(
      { assertThat(readMessage).isInstanceOf(AvroProtocol.TwoWayMessage::class.java) },
      { assertThat(readMessage.request).isEqualTo(EmptyType.schema) },
      { assertThat(readMessage.name.value).isEqualTo("read") },
      { assertThat(readMessage.request.type).isEqualTo(SchemaType.RECORD) },
      { assertThat(readMessage.request.name).isEqualTo(Name("ReadRequest")) },
    )
  }
}
