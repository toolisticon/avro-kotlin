package io.toolisticon.kotlin.avro.generator.api

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.avro.avro4k.generator.api.TestFixtures.parser
import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.avroClassName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class AvroKotlinGeneratorApiTest {

  @Test
  fun `classNames without suffix`() {
    val recordType = parser.parseSchema(
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
