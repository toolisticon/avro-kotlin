package io.toolisticon.kotlin.avro.serialization

import io.toolisticon.kotlin.avro.AvroKotlin.avroSchemaResolver
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.serialization._test.BarString
import io.toolisticon.kotlin.avro.serialization._test.DummyEnum
import io.toolisticon.kotlin.avro.serialization._test.Foo
import io.toolisticon.kotlin.avro.serialization._test.barStringSchema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroKotlinSerializationTest {

  private val avro = AvroKotlinSerialization()

  @Test
  fun `read schema from Foo`() {
    val schema = avro.schema(Foo::class)

    assertThat(schema.namespace.value).isEqualTo("io.toolisticon.kotlin.avro.serialization._test")
    assertThat(schema.name.value).isEqualTo("Foo")
    assertThat(schema.fields.map { it.name.value to it.type }).containsExactly(
      "name" to SchemaType.STRING
    )
  }

  @Test
  fun `encode and decoded single object encoded`() {
    val foo = Foo("name")

    val encoded = avro.encodeSingleObject(foo)

    val decoded: Foo = avro.decodeFromSingleObject(
      schemaResolver = avroSchemaResolver(avro.schema(Foo::class)),
      singleObjectEncodedBytes = encoded
    )

    assertThat(decoded).isEqualTo(foo)
  }

  @Test
  fun `enum from-to single object encoded`() {
    val data = DummyEnum.BAR

    val soe = avro.singleObjectEncoder<DummyEnum>().encode(data)

    val decoded = avro.singleObjectDecoder<DummyEnum>().decode(soe)

    assertThat(decoded).isEqualTo(data)
  }

  @Test
  fun `get schema from BarString`() {
    assertThat(avro.cachedSchemaClasses()).isEmpty()
    assertThat(avro.cachedSerializerClasses()).isEmpty()

    val schema = avro.schema(BarString::class)

    assertThat(schema.fingerprint).isEqualTo(barStringSchema.fingerprint)

    assertThat(avro.cachedSchemaClasses()).containsExactly(BarString::class)
    assertThat(avro.cachedSerializerClasses()).containsExactly(BarString::class)

    assertThat(avro[barStringSchema.fingerprint]).isEqualTo(schema)

    val data = BarString("foo")
    val encoded = avro.singleObjectEncoder<BarString>().encode(data)

    val decoded = avro.singleObjectDecoder<BarString>().decode(encoded)

    assertThat(decoded).isEqualTo(data)
  }

}

