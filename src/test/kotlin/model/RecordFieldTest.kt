package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchemaField
import io.toolisticon.avro.kotlin.value.Name
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
