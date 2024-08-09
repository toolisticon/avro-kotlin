package io.toolisticon.kotlin.avro.serialization.serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestBooleanLogicalType
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
    val record = avroSerialization.encodeToGenericRecord(data)
    assertThat(avroSerialization.decodeFromGenericRecord(record, Data::class)).isEqualTo(data)
  }

  @Test
  fun `reads forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.encodeToGenericRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(Data::class)).invoke(),
    )
    assertThat(avroSerialization.decodeFromGenericRecord(passedRecord, Data::class)).isEqualTo(data)
  }
}
