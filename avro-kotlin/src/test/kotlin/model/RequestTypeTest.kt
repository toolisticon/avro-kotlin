package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.value.Name
import org.apache.avro.Protocol
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class RequestTypeTest {

  private val protocol = Protocol.parse(
    """
      {
        "namespace": "foo",
        "protocol":"Bar",
        "messages": {
          "empty": {
            "request": [],
            "response": "null",
            "one-way": true
          },
          "sayHello": {
            "request": [{
               "name": "name",
               "type": "string"
            }],
            "response": "string"
          }
        }
      }
    """.trimIndent()
  )

  @Test
  fun `empty type`() {
    val type = RequestType.of(protocol.messages["empty"]!!)

    assertThat(type.name).isEqualTo(Name("EmptyRequest"))
    assertThat(type.isEmpty).isTrue()
    assertThat(type.isRoot).isFalse()
    assertThat(type.fields).isEmpty()
    assertThat(type.fingerprint).isEqualTo(EmptyType.fingerprint)
    assertThat(type.hashCode).isEqualTo(EmptyType.hashCode)
  }

  @Test
  fun `sayHelloRequest type`() {
    val type = RequestType.of(protocol.messages["sayHello"]!!)

    assertThat(type.name).isEqualTo(Name("SayHelloRequest"))
    assertThat(type.isEmpty).isFalse()
    assertThat(type.isRoot).isFalse()
    assertThat(type.fields).hasSize(1)
  }

  @Test
  fun `get empty request type from avroType() factory`() {
    val schema = RequestType.of(protocol.messages["empty"]!!).schema

    val type = AvroType.avroType<AvroType>(schema)
    assertThat(type).isInstanceOf(RequestType::class.java)
    assertThat((type as RequestType).isEmpty).isTrue()
  }

  @Test
  fun `get message request type from avroType() factory`() {
    val schema = RequestType.of(protocol.messages["sayHello"]!!).schema

    val type = AvroType.avroType<AvroType>(schema)
    assertThat(type).isInstanceOf(RequestType::class.java)
    assertThat((type as RequestType).isEmpty).isFalse()
  }
}
