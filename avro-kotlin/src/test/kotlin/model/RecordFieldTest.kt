package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaField
import io.toolisticon.kotlin.avro.value.Name
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RecordFieldTest {

  @Test
  fun `wrap schema field`() {
    val schema = AvroBuilder.primitiveSchema(SchemaType.STRING)
    val field = AvroSchemaField(Schema.Field("foo", schema.get()))
    val recordField = RecordField(field)

    assertThat(recordField.name).isEqualTo(Name("foo"))
    assertThat(recordField.type).isInstanceOf(StringType::class.java)
    assertThat(recordField.json.value).isEqualTo(
      """
      {
        "type" : "string"
      }
    """.trimIndent()
    )
  }
}
