package io.toolisticon.avro.kotlin.codec

import io.toolisticon.avro.kotlin._test.FooString
import io.toolisticon.avro.kotlin._test.FooString2
import io.toolisticon.avro.kotlin.codec.GenericRecordCodec.convert
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class GenericRecordCodecTest {

  @Test
  fun `encode fooString to json`() {
    val data = FooString("bar")

    val record = data.toGenericRecord()

    val json = GenericRecordCodec.encodeJson(record)

    assertThat(json.value).isEqualTo(
      """
      {
        "str" : "bar"
      }
    """.trimIndent()
    )

    val decoded = GenericRecordCodec.decodeJson(
      json = json,
      readerSchema = FooString.SCHEMA
    )

    assertThat(FooString(decoded)).isEqualTo(data)
  }

  @Test
  fun `encode fooString json - decode fooString2`() {
    val data = FooString("bar")
    val json = GenericRecordCodec.encodeJson(data.toGenericRecord())

    val decoded = GenericRecordJsonDecoder(
      readerSchema = FooString2.SCHEMA,
      writerSchema = FooString.SCHEMA
    ).decode(json)

    val fooString2 = FooString2(decoded)
    assertThat(fooString2.str).isEqualTo(data.str)
    assertThat(fooString2.uuid).isNull()
  }

  @Test
  fun `encode fooString to singleObject - readerSchema`() {
    val data = FooString("bar")
    val record = data.toGenericRecord()

    val encoded = GenericRecordCodec.encodeSingleObject(
      record = record
    )

    assertThat(encoded.hex).isEqualTo(FooString.SINGLE_OBJECT_BAR)

    val decoded = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = encoded,
      readerSchema = FooString.SCHEMA
    )

    assertThat(FooString(decoded)).isEqualTo(data)
  }

  @Test
  fun `convert - schema evolution fooString v1 to v2`() {
    val v2 = convert(
      FooString("foo").toGenericRecord(),
      FooString2.SCHEMA
    )

    with(FooString2(v2)) {
      assertThat(str).isEqualTo("foo")
      assertThat(uuid).isNull()
    }
  }
}
