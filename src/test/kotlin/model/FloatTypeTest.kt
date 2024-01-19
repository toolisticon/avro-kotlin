package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.SchemaType.FLOAT
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
