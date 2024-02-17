package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.model.SchemaType.STRING
import io.toolisticon.avro.kotlin.value.JsonString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class StringTypeTest {

  @Test
  fun `verify toString`() {
    val schema = AvroBuilder.primitiveSchema(type = STRING)
    val type = StringType(schema)

    assertThat(type).hasToString("StringType()")
  }

  @Test
  fun `stringType has valid jsonString`() {
    assertThat(AvroBuilder.primitiveSchema(type = STRING).json.value)
      .isEqualTo(JsonString.PRIMITIVE_TEMPLATE.format("\"string\""))
  }
}
