package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin.parseSchema
import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
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
