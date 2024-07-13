package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.repository.avroSchemaResolver
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
