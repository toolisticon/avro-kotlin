package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.repository.avroSchemaResolver
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestBytesLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

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

  @Serializable
  data class Data(
    @Serializable(with = TestBytesLogicalType.TypeSerializer::class)
    val byteValue: MyBytesType
  )

  private val data = Data(byteValue = MyBytesType("AB".toByteArray()))

  @Disabled("find out why this one is failing")
  @Test
  fun `reads forth and back`() {
    val record = avroSerialization.toRecord(data)
    assertThat(avroSerialization.fromRecord(record, Data::class)).isEqualTo(data)
  }

  @Test
  fun `reads forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.toRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(Data::class)).invoke(),
    )
    assertThat(avroSerialization.fromRecord(passedRecord, Data::class)).isEqualTo(data)
  }
}
