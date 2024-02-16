package io.toolisticon.avro.kotlin.model

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.SchemaType.BOOLEAN
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class RecordTypeTest {

  @Test
  fun `must be RECORD`() {
    assertThatThrownBy {
      RecordType(primitiveSchema(BOOLEAN))
    }.isInstanceOf(IllegalStateException::class.java)
  }


  @Test
  fun `create recordType`() {
    val resource = resourceUrl("schema/SimpleStringRecord.avsc")
    val schema = AvroKotlin.parseSchema(resource)

    val record: RecordType = AvroType.avroType(schema)

    assertThat(record.schema.isRecordType).isTrue()
  }


  @Test
  fun `schemaContainingUnionType has typesMap containing correct entries`() {
    val schema = AvroKotlin.parseSchema(resourceUrl("schema/SchemaContainingUnionType.avsc"))
    val record: RecordType = AvroType.avroType(schema)

    assertThat(record.typesMap).hasSize(7)

    assertThat(record.typesMap.values.map { it.schema.type }.toSet()).containsExactlyInAnyOrder(
      SchemaType.UNION,
      SchemaType.NULL,
      SchemaType.STRING,
      SchemaType.INT,
      SchemaType.RECORD,
    )
  }
}
