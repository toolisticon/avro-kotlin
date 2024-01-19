package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.SchemaType.NULL
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
