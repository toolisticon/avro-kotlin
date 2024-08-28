package io.toolisticon.kotlin.avro.model

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.kotlin.avro.TestFixtures.DEFAULT_PARSER
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.avro.value.Name
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MessageResponseTest {

  private val avpr = resourceUrl("protocol/MessageResponseTypeTest.avpr")
  private val declaration = DEFAULT_PARSER.parseProtocol(avpr)
  private val protocol = declaration.protocol

  @Test
  fun `protocol was read correctly`() {
    assertThat(protocol.messages).hasSize(4)
  }

  @Test
  fun `none for one-way`() {
    val messageName = Name("NONE")
    val message = requireNotNull(protocol.messages[messageName])
    assertThat(message.name).isEqualTo(messageName)
    assertThat(message).isInstanceOf(AvroProtocol.OneWayMessage::class.java)
    assertThat(message.response).isEqualTo(MessageResponse.NONE)
  }

  @Test
  fun `single for two-way with single response`() {
    val messageName = Name("SINGLE")
    val message = requireNotNull(protocol.messages[messageName])
    assertThat(message.name).isEqualTo(messageName)
    assertThat(message).isInstanceOf(AvroProtocol.TwoWayMessage::class.java)
    assertThat(message.response).isInstanceOf(MessageResponse.SINGLE::class.java)

    val type = AvroType.avroType<AvroType>(message.response.schema)

    assertThat(type).isInstanceOf(RecordType::class.java)
    assertThat(type.name).isEqualTo(Name("Result"))
  }

  @Test
  fun `multiple for two-way with array response`() {
    val messageName = Name("MULTIPLE")
    val message = requireNotNull(protocol.messages[messageName])
    assertThat(message.name).isEqualTo(messageName)
    assertThat(message).isInstanceOf(AvroProtocol.TwoWayMessage::class.java)
    assertThat(message.response).isInstanceOf(MessageResponse.MULTIPLE::class.java)

    val type = AvroType.avroType<AvroType>(message.response.schema)

    assertThat(type).isInstanceOf(RecordType::class.java)
    assertThat(type.name).isEqualTo(Name("Result"))
  }

  @Test
  fun `optional for two-way with nullable response`() {
    val messageName = Name("OPTIONAL")
    val message = requireNotNull(protocol.messages[messageName])
    assertThat(message.name).isEqualTo(messageName)
    assertThat(message).isInstanceOf(AvroProtocol.TwoWayMessage::class.java)
    assertThat(message.response).isInstanceOf(MessageResponse.OPTIONAL::class.java)

    val type = AvroType.avroType<AvroType>(message.response.schema)

    assertThat(type).isInstanceOf(RecordType::class.java)
    assertThat(type.name).isEqualTo(Name("Result"))
  }
}
