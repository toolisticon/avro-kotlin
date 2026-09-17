package serializer

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.codec.GenericRecordCodec
import io.toolisticon.kotlin.avro.serialization._test.BarString
import io.toolisticon.kotlin.avro.serialization._test.DummyEnum
import io.toolisticon.kotlin.avro.serialization._test.Foo
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.TestStringLogicalType
import io.toolisticon.kotlin.avro.serialization.serializer._fixtures.avroSerialization
import kotlinx.serialization.Serializable
import org.apache.avro.util.ClassSecurityValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import serializer.StringLogicalTypeSerializerTest.StringType

@Serializable
data class StringData(
  @Serializable(with = TestStringLogicalType.TypeSerializer::class)
  val stringValue: StringType
)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StringLogicalTypeSerializerTest {

  private lateinit var previousClassSecurityValidator: ClassSecurityValidator.ClassSecurityPredicate

  @BeforeAll
  fun trustBankAccountCreated() {
    previousClassSecurityValidator = ClassSecurityValidator.getGlobal()
    ClassSecurityValidator.setGlobal(
      ClassSecurityValidator.composite(
        ClassSecurityValidator.DEFAULT_TRUSTED_CLASSES,
        ClassSecurityValidator.builder()
          .add(StringData::class.java)
          .build()
      )
    )
  }

  @AfterAll
  fun restoreClassSecurityValidator() {
    ClassSecurityValidator.setGlobal(previousClassSecurityValidator)
  }

  data class StringType(val value: String)


  private val data = StringData(stringValue = StringType("stringType"))

  @Test
  fun `reads string forth and back`() {
    val record = avroSerialization.encodeToGenericRecord(data)
    assertThat(avroSerialization.decodeFromGenericRecord(record, StringData::class)).isEqualTo(data)
  }

  @Test
  fun `reads string forth and back from already converted logical type`() {
    val passedRecord = GenericRecordCodec.decodeSingleObject(
      singleObjectEncodedBytes = GenericRecordCodec.encodeSingleObject(avroSerialization.encodeToGenericRecord(data)),
      readerSchema = avroSchemaResolver(avroSerialization.schema(StringData::class)).invoke(),
    )
    assertThat(avroSerialization.decodeFromGenericRecord(passedRecord, StringData::class)).isEqualTo(data)
  }

  @Test
  fun `read string forth and back via avro4kSingleObject`() {
    val encoded = avroSerialization.encodeToSingleObjectEncoded(data)
    assertThat(avroSerialization.decodeFromSingleObjectEncoded<StringData>(encoded)).isEqualTo(data)
    val record = GenericRecordCodec.decodeSingleObject(encoded, avroSerialization.schema(StringData::class))
    assertThat(avroSerialization.decodeFromGenericRecord<StringData>(record)).isEqualTo(data)
  }
}
