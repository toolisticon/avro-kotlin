package serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestStringLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StringLogicalTypeSerializerTest {

  data class StringType(val value: String)

  @Serializable
  data class Data(
    @Serializable(with = TestStringLogicalType.TypeSerializer::class)
    val stringValue: StringType
  )

  private val data = Data(stringValue = StringType("stringType"))

  @Test
  fun `reads string forth and back`() {
    val record = avroSerialization.toRecord(data)
    assertThat(avroSerialization.fromRecord(record, Data::class)).isEqualTo(data)
  }

  @Test
  fun `reads string forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.toRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(Data::class)).invoke(),
    )
    assertThat(avroSerialization.fromRecord(passedRecord, Data::class)).isEqualTo(data)
  }
}
