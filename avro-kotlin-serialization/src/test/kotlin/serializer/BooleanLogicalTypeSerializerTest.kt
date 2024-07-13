package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.repository.avroSchemaResolver
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestBooleanLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestDoubleLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BooleanLogicalTypeSerializerTest {

  data class MyBooleanType(val value: Boolean)

  @Serializable
  data class Data(
    @Serializable(with = TestBooleanLogicalType.TypeSerializer::class)
    val intValue: MyBooleanType
  )

  private val data = Data(intValue = MyBooleanType(true))

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
