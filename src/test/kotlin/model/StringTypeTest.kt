package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import org.apache.avro.Schema.Type.STRING
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class StringTypeTest {

  @Test
  fun `verify toString`() {
    val schema = AvroBuilder.primitiveSchema(STRING)
    val type = StringType(schema)

    assertThat(type).hasToString("StringType()")
  }
}
