package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.repository.avroSchemaResolver
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestLongLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LongLogicalTypeSerializerTest {

  data class LongType(val value: Long)

  @Serializable
  data class Data(
    @Serializable(with = TestLongLogicalType.TypeSerializer::class)
    val longValue: LongType
  )

  private val data = Data(longValue = LongType(10))

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
