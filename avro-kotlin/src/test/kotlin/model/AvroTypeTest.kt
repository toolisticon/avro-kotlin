package io.toolisticon.kotlin.avro.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroTypeTest {

  @Test
  fun `list all subtypes`() {
    assertThat(avroTypes()).hasSize(26)
      .containsExactlyInAnyOrder(
        ArrayType::class,
        AvroContainerType::class,
        AvroNamedType::class,
        AvroNamedTypeWithFields::class,
        AvroPrimitiveType::class,
        AvroFieldType::class,
        BooleanType::class,
        BytesType::class,
        DoubleType::class,
        EmptyType::class,
        EnumType::class,
        ErrorType::class,
        ErrorField::class,
        FixedType::class,
        FloatType::class,
        IntType::class,
        LongType::class,
        MapType::class,
        NullType::class,
        OptionalType::class,
        RecordField::class,
        RecordType::class,
        RequestType::class,
        RequestField::class,
        StringType::class,
        UnionType::class,
      )
  }
}
