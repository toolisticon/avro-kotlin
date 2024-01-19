package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.builder.AvroBuilder.union
import io.toolisticon.avro.kotlin.model.SchemaType.NULL
import io.toolisticon.avro.kotlin.model.SchemaType.STRING
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

    assertThat(union.fingerprint).hasToString("362163B6A7722846")
    assertThat(union.types).hasSize(1)
    assertThat(union.isNullable).isFalse()
    assertThat(union).hasToString("UnionType(types=[string])")
  }

  @Test
  fun `nullable string union`() {
    val union = UnionType(union(primitiveSchema(NULL), primitiveSchema(STRING)))

    assertThat(union.fingerprint).hasToString("9845F21EB77EC49D")
    assertThat(union.types).hasSize(2)
    assertThat(union.isNullable).isTrue()
  }
}
