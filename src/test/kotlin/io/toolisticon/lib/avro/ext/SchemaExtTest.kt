package io.toolisticon.lib.avro.ext

import io.toolisticon.avro.kotlin._bak.SchemaExt.createGenericRecord
import io.toolisticon.avro.kotlin._bak.SchemaExt.fileExtension
import io.toolisticon.avro.kotlin._bak.SchemaExt.fingerprint
import io.toolisticon.avro.kotlin._bak.SchemaExt.path
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.apache.avro.generic.GenericData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

internal class SchemaExtTest {

  private val schemaFoo: Schema = SchemaBuilder
    .record("Foo")
    .namespace("test.lib")
    .fields().name("value").type("int")
    .noDefault()
    .endRecord()


  @Test
  fun `fingerprint of schema`() {
    assertThat(schemaFoo.fingerprint).isEqualTo(5240102248166447335L)
  }

  @Test
  fun `fileSuffix is avsc`() {
    assertThat(schemaFoo.fileExtension).isEqualTo("avsc")
  }

  @Test
  fun `expected path of schema file`() {
    assertThat(schemaFoo.path).isEqualTo(Path("test/lib/Foo.avsc"))
  }

  @Test
  fun `create generic record`() {
    val record: GenericData.Record = schemaFoo.createGenericRecord {
      put("value", 1)
    }

    assertThat(record["value"]).isEqualTo(1)
    assertThat(record.schema).isEqualTo(schemaFoo)
  }
}
