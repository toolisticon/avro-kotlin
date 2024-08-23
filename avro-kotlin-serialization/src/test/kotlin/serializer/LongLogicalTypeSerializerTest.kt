package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.serializer.LongLogicalTypeSerializerTest.LongType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestLongLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


@Serializable
data class LongData(
  @Serializable(with = TestLongLogicalType.TypeSerializer::class)
  val longValue: LongType
)

class LongLogicalTypeSerializerTest {

  data class LongType(val value: Long)


  private val data = LongData(longValue = LongType(10))

  @Test
  fun `reads forth and back`() {
    val record = avroSerialization.encodeToGenericRecord(data)
    assertThat(avroSerialization.decodeFromGenericRecord(record, LongData::class)).isEqualTo(data)
  }

  @Test
  fun `reads forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.encodeToGenericRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(LongData::class)).invoke(),
    )
    assertThat(avroSerialization.decodeFromGenericRecord(passedRecord, LongData::class)).isEqualTo(data)
  }


}
