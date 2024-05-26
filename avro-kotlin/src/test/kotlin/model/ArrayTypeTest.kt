package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder.array
import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.builder.AvroBuilder.union
import io.toolisticon.kotlin.avro.model.SchemaType.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

internal class ArrayTypeTest {

  @Test
  fun `verify array type`() {
    val schema = array(primitiveSchema(STRING))

    val type = ArrayType(schema)

    assertThat(type).hasToString("ArrayType(type=string)")
  }

  @Test
  fun `type map of array contains string and null`() {
    val schema = array(union(primitiveSchema(NULL), primitiveSchema(STRING)))
    val array: ArrayType = AvroType.avroType(schema)

    assertThat(array.typesMap).hasSize(3)
    assertThat(array.typesMap.values.map { it.schema.type }).containsExactlyInAnyOrder(UNION, NULL, STRING)
  }

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `arrayType jsonString`() {
    val json = array(primitiveSchema(STRING)).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "array",
        "items" : "string"
      }
    """.trimIndent()
    )
  }

  @Test
  fun `construction fails if not array`() {
    assertThatThrownBy {
      ArrayType(primitiveSchema(STRING))
    }.isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("Not an ARRAY type, was=STRING.")
  }
}
