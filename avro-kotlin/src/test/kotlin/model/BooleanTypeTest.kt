package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.BOOLEAN
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
    val json = primitiveSchema(BOOLEAN).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "boolean"
      }
    """.trimIndent()
    )
  }
}
