package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Name.Companion.toName
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RecordFieldTest {

  @Test
  fun `wrap schema field`() {
    val schema = SchemaBuilder.record("TestRecord")
      .namespace("io.toolisticon.kotlin.avro.model")
      .fields()
      .requiredString("foo")
      .endRecord()
    val type = RecordType(AvroSchema(schema))
    val recordField = type.getField("foo".toName())!!

    assertThat(recordField.name).isEqualTo(Name("foo"))
    assertThat(recordField.type).isInstanceOf(StringType::class.java)
    assertThat(recordField.json.value).isEqualTo(
      """
      {
        "type" : "string"
      }
    """.trimIndent()
    )
    assertThat(recordField.memberOf).isInstanceOf(RecordType::class.java)
  }
}
