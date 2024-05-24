package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin.parseSchema
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.builder.AvroBuilder.withLogicalType
import io.toolisticon.avro.kotlin.model.ErrorType
import io.toolisticon.avro.kotlin.model.SchemaType.STRING
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS


internal class JsonStringTest {

  @Test
  fun `parse jsonString from schema`() {
    val json = JsonString.of(
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
    assertThat(ErrorType(schema).isRoot).isFalse()
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `jsonString for string schema with logical type`() {
    val schema = primitiveSchema(STRING)
      .withLogicalType(LogicalTypes.uuid())

    assertThat(schema.json).isEqualTo(
      JsonString.of(
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
    val schema = primitiveSchema(STRING)

    assertThat(schema.json).isEqualTo(
      JsonString.of(
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

    assertThat(JsonString.of(schema)).isEqualTo(
      JsonString.of(
        """
        {
          "type" : "string"
        }""".trimIndent()
      )
    )
  }
}
