package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.model.SchemaType.*
import io.toolisticon.avro.kotlin.model.SchemaType.Companion.valueOfType
import org.apache.avro.Schema.Type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class SchemaTypeTest {

  @Test
  fun `primitive types`() {
    assertThat(SchemaType.PRIMITIVE_TYPES).containsExactlyInAnyOrder(
      BYTES, BOOLEAN, DOUBLE, FLOAT, STRING, NULL, INT, LONG
    )
  }

  @Test
  fun `named types`() {
    assertThat(SchemaType.NAMED_TYPES).containsExactlyInAnyOrder(
      RECORD, FIXED, ENUM
    )
  }

  @Test
  fun `container types`() {
    assertThat(SchemaType.CONTAINER_TYPES).containsExactlyInAnyOrder(
      ARRAY, MAP, UNION
    )
  }

  fun `value of type`() {
    assertThat(valueOfType(Type.RECORD)).isEqualTo(RECORD)
    assertThat(valueOfType(Type.ENUM)).isEqualTo(ENUM)
    assertThat(valueOfType(Type.FIXED)).isEqualTo(FIXED)
    assertThat(valueOfType(Type.ARRAY)).isEqualTo(ARRAY)
    assertThat(valueOfType(Type.MAP)).isEqualTo(MAP)
    assertThat(valueOfType(Type.UNION)).isEqualTo(UNION)
    assertThat(valueOfType(Type.BYTES)).isEqualTo(BYTES)
    assertThat(valueOfType(Type.INT)).isEqualTo(INT)
    assertThat(valueOfType(Type.LONG)).isEqualTo(LONG)
    assertThat(valueOfType(Type.FLOAT)).isEqualTo(FLOAT)
    assertThat(valueOfType(Type.DOUBLE)).isEqualTo(DOUBLE)
    assertThat(valueOfType(Type.BOOLEAN)).isEqualTo(BOOLEAN)
    assertThat(valueOfType(Type.NULL)).isEqualTo(NULL)
    assertThat(valueOfType(Type.STRING)).isEqualTo(STRING)
  }

  @Test
  fun `displayName is lowercase string from type`() {
    assertThat(STRING.displayName).isEqualTo(Type.STRING.getName())
  }
}
