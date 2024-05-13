package io.toolisticon.avro.kotlin.value

import _ktx.ResourceKtx.resourceUrl
import io.toolisticon.avro.kotlin.AvroKotlin.parseProtocol
import io.toolisticon.avro.kotlin.AvroKotlin.parseSchema
import io.toolisticon.avro.kotlin.AvroParser
import io.toolisticon.avro.kotlin._test.FooString
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.SchemaType.BYTES
import io.toolisticon.avro.kotlin.value.AvroFingerprint.Companion.NULL
import io.toolisticon.avro.kotlin.value.AvroFingerprint.Companion.readLong
import io.toolisticon.avro.kotlin.value.AvroFingerprint.Companion.sum
import io.toolisticon.avro.kotlin.value.AvroFingerprint.Companion.toBytes
import org.apache.avro.LogicalTypes.decimal
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

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
    val schema1 = parseSchema(
      JsonString(
        """
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
    """
      )
    )


    val schema2 = parseSchema(
      JsonString(
        """
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
    """
      )
    )

    assertThat(schema1.fingerprint).isEqualTo(schema2.fingerprint)
  }

  @Test
  fun `fingerprint of protocols is identical if only props and docs differ`() {
    val p1 = parseProtocol(resourceUrl("protocol/DummyProtocol.avpr"))
    val p2 = parseProtocol(resourceUrl("protocol/DummyProtocol2.avpr"))

    assertThat(p1.fingerprint).isEqualTo(p2.fingerprint)
  }

  @Test
  fun `single plus list`() {
    assertThat(AvroFingerprint(1L) + listOf(AvroFingerprint(2), AvroFingerprint(3))).isEqualTo(AvroFingerprint(6))
  }

  @Test
  fun `fingerprint is equal for decimal scale but hashCode differs`() {
    val dec1 = primitiveSchema(type = BYTES, logicalType = decimal(4, 2))
    val dec2 = primitiveSchema(type = BYTES, logicalType = decimal(6, 2), properties = ObjectProperties("foo" to "bar"))

    assertThat(dec1.fingerprint).isEqualTo(dec2.fingerprint)
    assertThat(dec1.hashCode).isNotEqualTo(dec2.hashCode)
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
    val r2 = AvroParser().parseSchema(
      JsonString(
        """
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
    """
      )
    )

    assertThat(AvroFingerprint(r2.schema.getField(Name("xxx"))!!))
      .isEqualTo(AvroFingerprint(r2.schema.getField(Name("yyy"))!!))
  }

  @Test
  fun `fingerprint of schema foo`() {

    val schemaFoo: Schema = SchemaBuilder
      .record("Foo")
      .namespace("test.lib")
      .fields().name("value").type("int")
      .noDefault()
      .endRecord()

    assertThat(AvroFingerprint(schemaFoo).value).isEqualTo(5240102248166447335L)
  }

  @Test
  fun `derive fingerprint from single object encoded bytes`() {
    val fp = FooString.SCHEMA.fingerprint

    val bytes = FooString.SINGLE_OBJECT_BAR.byteArray
    val fingerprint = AvroFingerprint(bytes)
    assertThat(fingerprint).isEqualTo(fp)
  }

  @Test
  fun `long from to bytes`() {
    val long = Random.nextLong()
    val bytes = long.toBytes()
    assertThat(bytes.readLong()).isEqualTo(long)
  }
}
