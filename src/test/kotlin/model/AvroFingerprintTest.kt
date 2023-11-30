package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroParser.Companion.parseSchema
import io.toolisticon.avro.kotlin.key.AvroFingerprint
import io.toolisticon.avro.kotlin.key.AvroHashCode
import io.toolisticon.avro.kotlin.key.AvroFingerprint.Companion.NULL
import io.toolisticon.avro.kotlin.key.AvroFingerprint.Companion.sum
import org.apache.avro.LogicalTypes
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroFingerprintTest {

  @Test
  fun `sum() of empty array is NULL`() {
    assertThat(listOf<AvroFingerprint>().sum()).isEqualTo(NULL)
  }

  @Test
  fun `NULL plus NULL is NULL`() {
    assertThat(NULL + NULL).isEqualTo(NULL)
    assertThat(listOf(NULL, NULL).sum()).isEqualTo(NULL)
  }

  @Test
  fun `fingerprint of string`() {
    val f = AvroFingerprint("Foo")
    assertThat(f.value).isEqualTo(-6559215080592870065L)

    assertThat(AvroFingerprint(null)).isEqualTo(AvroFingerprint(""))
  }

  @Test
  fun `fingerprint of schema`() {
    val f = AvroFingerprint(Schema.create(Schema.Type.STRING))
    assertThat(f.value).isEqualTo(-8142146995180207161L)
  }

  @Test
  fun `two schemas have the same fingerprint if we ignore additional fields`() {
    val schema1 = """
      {
        "name":"foo.bar.Dummy",
        "doc":"first dummy",
        "meta":{"foo":"bar"},
        "type":"error",
        "fields":[
          {
            "name":"foo",
            "type":"string"
          }
        ]
      }
    """.parseSchema()


    val schema2 = """
      {
        "name":"foo.bar.Dummy",
        "doc":"second  dummy",
        "meta":{"foo":"hello world"},
        "type":"error",
        "fields":[
          {
            "name":"foo",
            "type":"string"
          }
        ]
      }
    """.parseSchema()

    assertThat(AvroFingerprint(schema1)).isEqualTo(AvroFingerprint(schema2))
  }

  @Test
  fun `fingerprint of protocols is identical if only props and docs differ`() {
    val p1 = loadProtocolResource("protocol/DummyProtocol.avpr")
    val p2 = loadProtocolResource("protocol/DummyProtocol2.avpr")

    assertThat(AvroFingerprint(p1)).isEqualTo(AvroFingerprint(p2))
  }

  @Deprecated("remove")
  private fun loadProtocolResource(s: String): Protocol {
  TODO()
  }

  @Test
  fun `single plus list`() {
    assertThat(AvroFingerprint(1L) + listOf(AvroFingerprint(2), AvroFingerprint(3))).isEqualTo(AvroFingerprint(6))
  }

  @Test
  fun `fingerprint is equal for decimal scale but hashCode differs`() {
    val dec1 = Schema.create(Schema.Type.BYTES).apply {
      LogicalTypes.decimal(4, 2).addToSchema(this)
    }
    val dec2 = SchemaBuilder.builder()
      .bytesBuilder()
      .prop("foo", "bar")
      .endBytes()
      .apply {
        LogicalTypes.decimal(6, 2).addToSchema(this)
      }

    assertThat(AvroFingerprint(dec1)).isEqualTo(AvroFingerprint(dec2))
    assertThat(AvroHashCode(dec1)).isNotEqualTo(AvroHashCode(dec2))
    assertThat(dec1).isNotEqualTo(dec2)
  }

  @Test
  fun `is doc included in hashCode()`() {
    val r1 = SchemaBuilder.record("foo.Bar")
      .doc("xxx")
      .fields().nullableBoolean("yyy", true)
      .endRecord()
    val r2 = SchemaBuilder.record("foo.Bar")
      .doc("xxx")
      .fields().nullableBoolean("yyy", true)
      .endRecord()

    assertThat(AvroFingerprint(r1)).isEqualTo(AvroFingerprint(r2))
    assertThat(r1.hashCode()).isEqualTo(r2.hashCode())
  }

  @Test
  fun `use record`() {
    val r2 = """
      {
        "name":"foo.Foo",
        "type":"record",
        "fields":[
          {
            "name":"xxx",
            "type": {
              "name":"Bar",
              "type": "record",
              "fields":[
                {
                  "name":"bar",
                  "type":"string"
                }
              ]
            }
          },
          {"name":"yyy","type":"foo.Bar"}
        ]
      }
    """.parseSchema()

    assertThat(AvroFingerprint(r2.getField("xxx")))
      .isEqualTo(AvroFingerprint(r2.getField("yyy")))
  }

}
