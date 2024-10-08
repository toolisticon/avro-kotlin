package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.serializer.BytesLogicalTypeSerializerTest.MyBytesType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestBytesLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test


@Serializable
data class BytesData(
  @Serializable(with = TestBytesLogicalType.TypeSerializer::class)
  val byteValue: MyBytesType
)

class BytesLogicalTypeSerializerTest {

  data class MyBytesType(val value: ByteArray) {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as MyBytesType

      return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
      return value.contentHashCode()
    }
  }

  private val data = BytesData(byteValue = MyBytesType("AB".toByteArray()))

  @Disabled("find out why this one is failing")
  @Test
  fun `reads forth and back`() {
    val record = avroSerialization.encodeToGenericRecord(data)
    assertThat(avroSerialization.decodeFromGenericRecord(record, BytesData::class)).isEqualTo(data)
  }

  @Test
  fun `reads forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.encodeToGenericRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(BytesData::class)).invoke(),
    )
    assertThat(avroSerialization.decodeFromGenericRecord(passedRecord, BytesData::class)).isEqualTo(data)
  }
}
