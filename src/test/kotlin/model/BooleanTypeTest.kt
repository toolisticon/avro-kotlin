package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.SchemaType.BOOLEAN
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BooleanTypeTest {

  @Test
  fun `verify toString`() {
    val schema = primitiveSchema(BOOLEAN)
    val type = BooleanType(schema)

    assertThat(type).hasToString("BooleanType()")
  }

  @Test
  fun `booleanType jsonString`() {
    val json = primitiveSchema(SchemaType.BOOLEAN).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "boolean"
      }
    """.trimIndent()
    )
  }
}
