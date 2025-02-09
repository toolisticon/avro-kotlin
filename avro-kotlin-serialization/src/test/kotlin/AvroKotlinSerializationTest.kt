package io.toolisticon.kotlin.avro.serialization

import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization.Companion.version
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
  fun parseVersion() {
    val version = version("1.8.0")
    assertThat(version.version()).containsExactly(1, 8)
  }

  @Test
  fun `encode and decoded single object encoded`() {
    val foo = Foo("name")

    val encoded = avro.encodeToSingleObjectEncoded(foo)
    avro.registerSchema(avro.schema(Foo::class))

    val decoded: Foo = avro.decodeFromSingleObjectEncoded(encoded = encoded)

    assertThat(decoded).isEqualTo(foo)
  }

  @Test
  fun `enum from-to single object encoded`() {
    val data = DummyEnum.BAR

    val soe = avro.avro4kSingleObjectCodec.encoder<DummyEnum>().encode(data)

    val decoded = avro.avro4kSingleObjectCodec.decoder<DummyEnum>().decode(soe)

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
    val encoded = avro.avro4kSingleObjectCodec.encoder<BarString>().encode(data)

    val decoded = avro.avro4kSingleObjectCodec.decoder<BarString>().decode(encoded)

    assertThat(decoded).isEqualTo(data)
  }

  @Test
  fun `deserialize with anonymous class`() {
    val foo = Foo("name")
    val encoded = avro.encodeToSingleObjectEncoded(foo)

    val anym = Class.forName(Foo::class.qualifiedName!!)
    println(anym)
    val decoded = avro.decodeFromSingleObjectEncoded(encoded, anym.kotlin)
    println(decoded)
  }
}

