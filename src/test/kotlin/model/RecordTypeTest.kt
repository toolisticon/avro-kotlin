package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test


internal class RecordTypeTest {

  @Test
  fun `must be RECORD`() {
    assertThatThrownBy {
      RecordType(primitiveSchema(Schema.Type.BOOLEAN))
    }.isInstanceOf(IllegalStateException::class.java)
  }
}
