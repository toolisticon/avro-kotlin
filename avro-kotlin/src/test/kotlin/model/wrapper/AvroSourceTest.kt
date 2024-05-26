package io.toolisticon.kotlin.avro.model.wrapper

import io.toolisticon.kotlin.avro.builder.AvroBuilder
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.value.AvroSpecification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroSourceTest {

  @Test
  fun `create from schema json`() {
    val json = AvroBuilder.primitiveSchema(SchemaType.STRING).json
    val source = JsonSource(json, AvroSpecification.SCHEMA)


    assertThat(source).isInstanceOf(JsonSource::class.java)
  }
}
