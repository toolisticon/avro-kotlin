package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin.parseSchema
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.recordType
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class JsonStringTest {

  @Test
  fun `parse jsonString from schema`() {
    val json = JsonString(
      """

      {
        "name":"foo.bar.Dummy",
        "doc":"first dummy",
        "meta":{"foo":"bar"},
        "type":"error",
        "fields":[
          {
            "name":"foo",
            "type":"string"
          }
        ]
      }
    """.trimIndent()
    )

    assertThat(json.value)
      .startsWith("{")
      .endsWith("}")

    val schema = parseSchema(json)
    assertThat(schema.recordType().isRoot).isFalse()
  }

  @Test
  fun `jsonString for string schema with logical type`() {
    val schema = primitiveSchema(Schema.Type.STRING)
      .withLogicalType(LogicalTypes.uuid())

    assertThat(schema.json).isEqualTo(
      JsonString(
        """
      {
        "type" : "string",
        "logicalType" : "uuid"
      }""".trimIndent()
      )
    )
  }

  @Test
  fun `jsonString for simple string schema`() {
    val schema = primitiveSchema(Schema.Type.STRING)

    assertThat(schema.json).isEqualTo(
      JsonString(
        """
        {
          "type" : "string"
        }""".trimIndent()
      )
    )
  }

  @Test
  fun `parse schema`() {
    val schema = Schema.Parser().parse("""{"type":"string"}""")

    println(schema)
  }
}
