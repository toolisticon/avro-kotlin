package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.FLOAT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FloatTypeTest {

  @Test
  fun `floatType with simple jsonString`() {
    val json = primitiveSchema(FLOAT).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "float"
      }
    """.trimIndent()
    )
  }
}
