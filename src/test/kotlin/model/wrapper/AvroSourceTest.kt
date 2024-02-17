package io.toolisticon.avro.kotlin.model.wrapper

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.value.AvroSpecification
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
