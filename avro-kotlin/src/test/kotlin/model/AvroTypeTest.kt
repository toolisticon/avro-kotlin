package io.toolisticon.kotlin.avro.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroTypeTest {

  @Test
  fun `list all subtypes`() {
    assertThat(avroTypes()).hasSize(21)
      .containsExactlyInAnyOrder(
        ArrayType::class,
        MapType::class,
        UnionType::class,
        EmptyType::class,
        EnumType::class,
        ErrorType::class,
        FixedType::class,
        RecordType::class,
        RecordField::class,
        BooleanType::class,
        BytesType::class,
        DoubleType::class,
        FloatType::class,
        IntType::class,
        LongType::class,
        NullType::class,
        OptionalType::class,
        StringType::class,
        AvroContainerType::class,
        AvroNamedType::class,
        AvroPrimitiveType::class,
      )

  }
}
