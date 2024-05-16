package io.toolisticon.avro.kotlin.logical

import io.toolisticon.avro.kotlin.logical.conversion.StringLogicalTypeConversion
import io.toolisticon.avro.kotlin.value.LogicalTypeName.Companion.toLogicalTypeName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


/**
 * Here we test a dummy string localtype and its factory and conversion to prove that
 * everything works as expected for a [AvroLogicalType].
 */
internal class StringLogicalTypeFactoryTest {
  object DummyStringLogicalType : StringLogicalType("dummy".toLogicalTypeName())

  class DummyStringLogicalTypeFactory : StringLogicalTypeFactory<DummyStringLogicalType>(DummyStringLogicalType)

  class DummyStringConversion : StringLogicalTypeConversion<DummyStringLogicalType, Long>(logicalType = DummyStringLogicalType, convertedType = Long::class) {
    override fun fromAvro(value: String) = value.toLong()
    override fun toAvro(value: Long) = value.toString()
  }


  @Test
  fun `type has correct toString`() {
    assertThat(DummyStringLogicalType).hasToString("DummyStringLogicalType(name='dummy', type=STRING)")
  }

  @Test
  fun `factory has correct toString`() {
    assertThat(DummyStringLogicalTypeFactory()).hasToString("DummyStringLogicalTypeFactory(name='dummy', type=STRING)")
  }

  @Test
  fun `conversion has correct toString`() {
    assertThat(DummyStringConversion()).hasToString("DummyStringConversion(logicalType='dummy', schemaType=STRING, convertedType=kotlin.Long)")
  }

  @Test
  fun `convert from and to dummy`() {
    val conversion = DummyStringConversion()
    assertThat(conversion.toAvro(5L)).isEqualTo("5")
    assertThat(conversion.fromAvro("5")).isEqualTo(5)
  }
}
