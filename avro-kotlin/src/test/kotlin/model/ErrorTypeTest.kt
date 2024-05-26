package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.AvroKotlin.parseSchema
import io.toolisticon.kotlin.avro.value.JsonString
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ErrorTypeTest {

  @Test
  fun `recordType for error schema`() {
    val json = JsonString.of(
      """
      {
        "name":"foo.bar.DummyException",
        "type":"error",
        "fields":[
          {
            "name":"message",
            "type":"string"
          }
        ]
      }
    """.trimIndent()
    )

    val schema = parseSchema(json)
    val type: ErrorType = AvroType.avroType(schema)

    assertThat(type.canonicalName).isEqualTo(Namespace("foo.bar") + Name("DummyException"))
  }
}
