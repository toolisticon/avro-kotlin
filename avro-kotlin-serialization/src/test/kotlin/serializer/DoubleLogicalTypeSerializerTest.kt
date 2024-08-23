package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.serializer.DoubleLogicalTypeSerializerTest.MyDoubleType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestDoubleLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


@Serializable
data class DoubleData(
  @Serializable(with = TestDoubleLogicalType.TypeSerializer::class)
  val intValue: MyDoubleType
)

class DoubleLogicalTypeSerializerTest {

  data class MyDoubleType(val value: Double)

  private val data = DoubleData(intValue = MyDoubleType(10.1))

  @Test
  fun `reads forth and back`() {
    val record = avroSerialization.encodeToGenericRecord(data)
    assertThat(avroSerialization.decodeFromGenericRecord(record, DoubleData::class)).isEqualTo(data)
  }

  @Test
  fun `reads forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.encodeToGenericRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(DoubleData::class)).invoke(),
    )
    assertThat(avroSerialization.decodeFromGenericRecord(passedRecord, DoubleData::class)).isEqualTo(data)
  }
}
