package io.toolisticon.kotlin.avro.generator.test

import com.github.avrokotlin.avro4k.Avro
import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import org.junit.jupiter.api.Test

class NestedFooStringTest {

  private val avro = AvroKotlinSerialization(avro4k = Avro.Default)

  @Test
  fun `load class`() {
    val schema = avro.schema(NestedFooStringData::class)
    val s2 = avro.schema(NestedFooStringData.BarStringData::class)

    println(schema)
    println(s2)

    val x = AvroKotlin.loadClassForSchema<NestedFooStringData.BarStringData>(s2)
    println(x)
    println(Class.forName("io.toolisticon.kotlin.avro.generator.test.NestedFooStringData\$BarStringData"))
  }
}
