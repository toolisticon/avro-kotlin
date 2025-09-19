package io.toolisticon.kotlin.avro.serialization

import com.github.avrokotlin.avro4k.*
import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.serialization._test.DummyEnum
import io.toolisticon.kotlin.avro.serialization.avro4k.Avro4kSchemaRegistry
import io.toolisticon.kotlin.avro.value.SingleObjectEncodedBytes
import kotlinx.serialization.ExperimentalSerializationApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@OptIn(ExperimentalSerializationApi::class, ExperimentalAvro4kApi::class)
internal class Avro4kSerializationTest {

  private val avro4k = Avro.Default

  @Test
  fun `serialize enum to single object`() {
    val schema = AvroSchema(Avro.schema<DummyEnum>())
    val resolver = avroSchemaResolver(schema)

    val avro4kSingleObject = AvroSingleObject(schemaRegistry = Avro4kSchemaRegistry(resolver), avro = avro4k)

    val bytes = SingleObjectEncodedBytes.of(avro4kSingleObject.encodeToByteArray(DummyEnum.BAR))

    val value = avro4kSingleObject.decodeFromByteArray<DummyEnum>(bytes.value)

    assertThat(value).isEqualTo(DummyEnum.BAR)
  }
}
