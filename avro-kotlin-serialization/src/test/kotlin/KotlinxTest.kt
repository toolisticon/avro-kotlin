package io.toolisticon.kotlin.avro.serialization

import org.junit.jupiter.api.Test
import kotlin.reflect.full.createType

internal class KotlinxTest {


  @Test
  fun `ktype spikes`() {
    val ktype = String::class.createType()

    println(ktype)
  }
}
