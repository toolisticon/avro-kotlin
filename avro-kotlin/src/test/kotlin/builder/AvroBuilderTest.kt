package io.toolisticon.kotlin.avro.builder

import io.toolisticon.kotlin.avro.model.SchemaType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroBuilderTest {

  @Test
  fun `built in schema for uuid`() {
    val schema = AvroBuilder.SCHEMA_UUID

    assertThat(schema.type).isEqualTo(SchemaType.STRING)
    assertThat(schema.logicalTypeName?.value).isEqualTo("uuid")
    assertThat(schema.logicalType).isNotNull
  }
}
