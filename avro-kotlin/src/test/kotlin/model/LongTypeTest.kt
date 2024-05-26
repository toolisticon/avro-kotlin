package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.LONG
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class LongTypeTest {

  @Test
  fun `longType with simple jsonString`() {
    val json = primitiveSchema(LONG).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "long"
      }
    """.trimIndent()
    )
  }
}
