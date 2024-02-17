package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.model.SchemaType.*
import io.toolisticon.avro.kotlin.model.SchemaType.Companion.valueOfType
import io.toolisticon.avro.kotlin.model.SchemaType.Group.*
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


internal class SchemaTypeTest {

  @Test
  fun `primitive types`() {
    assertThat(SchemaType.PRIMITIVE_TYPES).containsExactlyInAnyOrder(
      BYTES, BOOLEAN, DOUBLE, FLOAT, STRING, NULL, INT, LONG
    )
  }

  @Test
  fun `named types`() {
    assertThat(SchemaType.valuesOfGroup(NAMED)).containsExactlyInAnyOrder(
      RECORD, FIXED, ENUM
    )
  }

  @Test
  fun `container types`() {
    assertThat(SchemaType.valuesOfGroup(CONTAINER)).containsExactlyInAnyOrder(
      ARRAY, MAP, UNION
    )
  }

  @Test
  fun `types in group NAMED`() {
    assertThat(NAMED.types).containsExactlyInAnyOrder(
      RECORD, FIXED, ENUM
    )
  }

  @Test
  fun `types in group CONTAINER`() {
    assertThat(CONTAINER.types).containsExactlyInAnyOrder(
      ARRAY, MAP, UNION
    )
  }

  @Test
  fun `types in group PRIMITIVE`() {
    assertThat(PRIMITIVE.types).containsExactlyInAnyOrder(
      BYTES, BOOLEAN, DOUBLE, FLOAT, STRING, NULL, INT, LONG
    )
  }

  @ParameterizedTest
  @CsvSource(
    value = [
      "RECORD,RECORD",
      "ENUM,ENUM",
      "FIXED,FIXED",
      "ARRAY,ARRAY",
      "MAP,MAP",
      "UNION,UNION",
      "BYTES,BYTES",
      "INT,INT",
      "LONG,LONG",
      "FLOAT,FLOAT",
      "DOUBLE,DOUBLE",
      "BOOLEAN,BOOLEAN",
      "NULL,NULL",
      "STRING,STRING",
    ]
  )
  fun `value of type`(avro: Schema.Type, expected: SchemaType) {
    assertThat(valueOfType(avro)).isEqualTo(expected)
  }

  @Test
  fun `displayName is lowercase string from type`() {
    assertThat(STRING.displayName).isEqualTo(Schema.Type.STRING.getName())
  }
}
