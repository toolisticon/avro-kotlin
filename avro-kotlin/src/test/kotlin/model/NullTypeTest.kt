package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.NULL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class NullTypeTest {

  @Test
  fun `nullType with simple jsonString`() {
    val json = primitiveSchema(NULL).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "null"
      }
    """.trimIndent()
    )
  }
}
