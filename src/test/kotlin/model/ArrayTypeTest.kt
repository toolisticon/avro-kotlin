package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder.array
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import org.apache.avro.Schema.Type.STRING
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ArrayTypeTest {

  @Test
  fun `verify array type`() {
    val schema = array(primitiveSchema(STRING))

    val type = ArrayType(schema)

    assertThat(type).hasToString("ArrayType(type=string)")
  }
}
