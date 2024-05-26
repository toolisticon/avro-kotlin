package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.INT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class IntTypeTest {

  @Test
  fun `intType with simple jsonString`() {
    val json = primitiveSchema(INT).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "int"
      }
    """.trimIndent()
    )
  }
}
