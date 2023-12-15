package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import org.apache.avro.Schema.Type.STRING
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MapTypeTest {

  @Test
  fun `verify toString`() {
    val schema = AvroBuilder.map(primitiveSchema(STRING))
    val type = MapType(schema)

    assertThat(type).hasToString("MapType(type=string)")
  }
}
