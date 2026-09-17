package io.toolisticon.kotlin.avro.generator.test

import com.github.avrokotlin.avro4k.Avro
import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.serialization.AvroKotlinSerialization
import org.apache.avro.util.ClassSecurityValidator
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NestedFooStringTest {

  private lateinit var previousClassSecurityValidator: ClassSecurityValidator.ClassSecurityPredicate

  @BeforeAll
  fun trustBankAccountCreated() {
    previousClassSecurityValidator = ClassSecurityValidator.getGlobal()
    ClassSecurityValidator.setGlobal(
      ClassSecurityValidator.composite(
        ClassSecurityValidator.DEFAULT_TRUSTED_CLASSES,
        ClassSecurityValidator.builder()
          .add(NestedFooStringData::class.java)
          .add(BarString::class.java)
          .build()
      )
    )
  }

  @AfterAll
  fun restoreClassSecurityValidator() {
    ClassSecurityValidator.setGlobal(previousClassSecurityValidator)
  }

  private val avro = AvroKotlinSerialization(avro4k = Avro.Default)

  @Test
  fun `load class`() {
    val schema = avro.schema(NestedFooStringData::class)
    val s2 = avro.schema(BarStringData::class)

    println(schema)
    println(s2)

    val x = AvroKotlin.loadClassForSchema<BarStringData>(s2)
    println(x)
    println(Class.forName("io.toolisticon.kotlin.avro.generator.test.BarStringData"))
  }
}
