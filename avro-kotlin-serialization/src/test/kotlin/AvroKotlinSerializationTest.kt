package io.toolisticon.kotlin.avro.serialization

import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.repository.avroSchemaResolver
import io.toolisticon.kotlin.avro.serialization._test.DummyEnum
import io.toolisticon.kotlin.avro.serialization._test.Foo
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

    println(encoded.hex.formatted)

    val decoded: Foo = avro.decodeFromSingleObject(
      schemaResolver = avroSchemaResolver(avro.schema(Foo::class)),
      singleObjectEncodedBytes = encoded
    )

    assertThat(decoded).isEqualTo(foo)
  }

  @Test
  fun `enum from-to single object encoded`() {
    val data = DummyEnum.BAR

    val soe = avro.toSingleObjectEncoded(data)


  }
}
