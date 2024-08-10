package io.toolisticon.kotlin.avro.serialization.avro4k

import com.github.avrokotlin.avro4k.Avro
import io.toolisticon.kotlin.avro.serialization._test.BarString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class Avro4kReflectionTest {

  @Test
  fun `read from cache`() {
    val avro: Avro = Avro {}
    assertThat(avro.schemaCache()).isEmpty()

    val schema = avro.schema(BarString.serializer().descriptor)

    assertThat(avro.schemaCache()).hasSize(1).containsValue(schema)
  }
}
