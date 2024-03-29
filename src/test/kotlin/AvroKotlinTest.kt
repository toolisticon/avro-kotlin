package io.toolisticon.avro.kotlin

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.avro.kotlin.AvroKotlin.createGenericRecord
import io.toolisticon.avro.kotlin.model.wrapper.AvroProtocol
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroSpecification
import io.toolisticon.avro.kotlin.value.Directory
import org.apache.avro.SchemaBuilder
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.io.path.Path

internal class AvroKotlinTest {

  @TempDir
  private lateinit var tmpDir: File

  private val directory: Directory by lazy {
    Directory(tmpDir)
  }

  @Test
  fun `can load protocol from resources`() {
    val cn = TestFixtures.fqnFindCurrentBalance
    val url = resourceUrl(Path("/avro").resolve(cn.toPath(AvroSpecification.PROTOCOL)))

    val protocol: AvroProtocol = AvroKotlin.parseProtocol(url)

    assertThat((protocol.namespace)).isEqualTo(cn.namespace)
    assertThat((protocol.name)).isEqualTo(cn.name)
  }


  @Test
  fun `can load schema from resources via fqn`() {
    val cn = TestFixtures.fqnBankAccountCreated
    val url = resourceUrl(
      Path("/avro").resolve(cn.toPath(AvroSpecification.SCHEMA))
    )

    val schema: AvroSchema = AvroKotlin.parseSchema(url)

    assertThat(schema).isNotNull
    assertThat((schema.namespace)).isEqualTo(cn.namespace)
    assertThat((schema.name)).isEqualTo(cn.name)
  }

  @Test
  fun `can write schema to file (and read again)`() {
    val schema = AvroParser().parseSchema(resourceUrl("schema/SimpleStringRecord.avsc"))

    AvroKotlin.write(schema, directory)

    val readFromFile = AvroParser().parseSchema(tmpDir.resolve("io/acme/schema/SimpleStringRecord.avsc"))

    assertThat(readFromFile).isEqualTo(schema)
  }

  @Test
  fun `can write protocol to file (and read again)`() {
    val protocol = AvroParser().parseProtocol(resourceUrl("protocol/DummyProtocol.avpr"))

    AvroKotlin.write(protocol, directory)

    val readFromFile = AvroParser().parseProtocol(tmpDir.resolve("foo/dummy/DummyProtocol.avpr"))

    assertThat(readFromFile).isEqualTo(protocol)
  }


  @Test
  fun `create generic record`() {
    val schemaFoo: AvroSchema = AvroSchema(
      SchemaBuilder
        .record("Foo")
        .namespace("test.lib")
        .fields().name("value").type("int")
        .noDefault()
        .endRecord()
    )


    val record: GenericData.Record = createGenericRecord(schemaFoo) {
      put("value", 1)
    }

    assertThat(record["value"]).isEqualTo(1)
    assertThat(record.schema).isEqualTo(schemaFoo.get())
  }
}
