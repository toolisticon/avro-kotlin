package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.builder.AvroBuilder.union
import io.toolisticon.kotlin.avro.model.SchemaType.NULL
import io.toolisticon.kotlin.avro.model.SchemaType.STRING
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class UnionTypeTest {

  @Test
  fun `empty union fails`() {

    assertThatThrownBy {
      UnionType(union())
    }.isInstanceOf(IllegalArgumentException::class.java)
  }

  @Test
  fun `string union`() {
    val union = UnionType(union(primitiveSchema(STRING)))

    assertThat(union.fingerprint).hasToString("462872A7B6632136")
    assertThat(union.types).hasSize(1)
    assertThat(union.isNullable).isFalse()
    assertThat(union).hasToString("UnionType(types=[string])")
  }

  @Test
  fun `nullable string union`() {
    val union = UnionType(union(primitiveSchema(NULL), primitiveSchema(STRING)))

    assertThat(union.fingerprint).hasToString("9DC47EB71EF24598")
    assertThat(union.types).hasSize(2)
    assertThat(union.isNullable).isTrue()
  }
}
