package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import org.apache.avro.Schema.Type.BOOLEAN
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BooleanTypeTest {

  @Test
  fun `verify toString`() {
    val schema = primitiveSchema(BOOLEAN)
    val type = BooleanType(schema)

    assertThat(type).hasToString("BooleanType()")
  }
}
