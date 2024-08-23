package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.serializer.FloatLogicalTypeSerializerTest.MyFloatType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestFloatLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


@Serializable
data class FloatData(
  @Serializable(with = TestFloatLogicalType.TypeSerializer::class)
  val intValue: MyFloatType
)

class FloatLogicalTypeSerializerTest {

  data class MyFloatType(val value: Float)

  private val data = FloatData(intValue = MyFloatType(10.1f))

  @Test
  fun `reads forth and back`() {
    val record = avroSerialization.encodeToGenericRecord(data)
    assertThat(avroSerialization.decodeFromGenericRecord(record, FloatData::class)).isEqualTo(data)
  }

  @Test
  fun `reads forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.encodeToGenericRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(FloatData::class)).invoke(),
    )
    assertThat(avroSerialization.decodeFromGenericRecord(passedRecord, FloatData::class)).isEqualTo(data)
  }
}
