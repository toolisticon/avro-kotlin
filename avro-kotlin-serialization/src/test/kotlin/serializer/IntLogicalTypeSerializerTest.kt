package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.serializer.IntLogicalTypeSerializerTest.MyIntType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestIntLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


@Serializable
data class IntData(
  @Serializable(with = TestIntLogicalType.TypeSerializer::class)
  val intValue: MyIntType
)

class IntLogicalTypeSerializerTest {

  data class MyIntType(val value: Int)

  private val data = IntData(intValue = MyIntType(10))

  @Test
  fun `reads forth and back`() {
    val record = avroSerialization.encodeToGenericRecord(data)
    assertThat(avroSerialization.decodeFromGenericRecord(record, IntData::class)).isEqualTo(data)
  }

  @Test
  fun `reads forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.encodeToGenericRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(IntData::class)).invoke(),
    )
    assertThat(avroSerialization.decodeFromGenericRecord(passedRecord, IntData::class)).isEqualTo(data)
  }


}
