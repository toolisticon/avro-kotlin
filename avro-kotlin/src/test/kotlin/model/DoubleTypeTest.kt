package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.DOUBLE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DoubleTypeTest {

  @Test
  fun `doubleType jsonString`() {
    val json = primitiveSchema(DOUBLE).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "double"
      }
    """.trimIndent()
    )
  }
}

