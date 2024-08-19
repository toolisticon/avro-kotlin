package io.toolisticon.kotlin.avro.generator

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.kotlin.avro.AvroParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FnTest {
  @Test
  fun `classNames without suffix`() {
    val recordType = TestFixtures.parser.parseSchema(
      resourceUrl("schema/SimpleStringRecord.avsc")
    ).recordType
    val properties = AvroKotlinGeneratorProperties(
      schemaTypeSuffix = ""
    )

    val className = avroClassName(recordType, properties)

    assertThat(className.canonicalName).isEqualTo("io.acme.schema.SimpleStringRecord")
  }

  @Test
  fun `classNames with suffix`() {
    val recordType = AvroParser().parseSchema(
      resourceUrl("schema/SimpleStringRecord.avsc")
    ).recordType
    val properties = AvroKotlinGeneratorProperties(
      schemaTypeSuffix = "Dto"
    )

    val className = avroClassName(recordType, properties)

    assertThat(className.canonicalName).isEqualTo("io.acme.schema.SimpleStringRecordDto")
  }
}
