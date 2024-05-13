package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.SchemaType.INT
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
