package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestIntLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class IntLogicalTypeSerializerTest {

  data class MyIntType(val value: Int)

  @Serializable
  data class Data(
    @Serializable(with = TestIntLogicalType.TypeSerializer::class)
    val intValue: MyIntType
  )

  private val data = Data(intValue = MyIntType(10))

  @Test
  fun `reads forth and back`() {
    val record = avroSerialization.toGenericRecord(data)
    assertThat(avroSerialization.fromGenericRecord(record, Data::class)).isEqualTo(data)
  }

  @Test
  fun `reads forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.toGenericRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(Data::class)).invoke(),
    )
    assertThat(avroSerialization.fromGenericRecord(passedRecord, Data::class)).isEqualTo(data)
  }


}
