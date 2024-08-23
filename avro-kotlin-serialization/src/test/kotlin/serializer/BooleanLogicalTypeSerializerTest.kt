package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.serializer.BooleanLogicalTypeSerializerTest.MyBooleanType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestBooleanLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


@Serializable
data class BooleanData(
  @Serializable(with = TestBooleanLogicalType.TypeSerializer::class)
  val intValue: MyBooleanType
)

class BooleanLogicalTypeSerializerTest {

  data class MyBooleanType(val value: Boolean)


  private val data = BooleanData(intValue = MyBooleanType(true))

  @Test
  fun `reads forth and back`() {
    val record = avroSerialization.encodeToGenericRecord(data)
    assertThat(avroSerialization.decodeFromGenericRecord(record, BooleanData::class)).isEqualTo(data)
  }

  @Test
  fun `reads forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.encodeToGenericRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(BooleanData::class)).invoke(),
    )
    assertThat(avroSerialization.decodeFromGenericRecord(passedRecord, BooleanData::class)).isEqualTo(data)
  }
}
