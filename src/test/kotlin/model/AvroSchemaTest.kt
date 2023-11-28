package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroBuilder
import io.toolisticon.avro.kotlin.value.Name
import org.apache.avro.Schema.Type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroSchemaTest {

  @Test
  fun `no exceptions for primitive string`() {
    val schema = AvroBuilder.primitiveSchema(Type.STRING)

    with(schema) {
      assertThat(aliases).isEmpty()
      assertThat(schema.fullName).isEqualTo("string")
      assertThat(schema.name).isEqualTo(Name("string"))
      assertThat(schema.namespace.isEmpty()).isTrue()
      assertThat(schema.type).isEqualTo(Type.STRING)

      assertThat(unionTypes).isEmpty()
      assertThat(enumSymbols).isEmpty()

      assertThat(arrayType).isNull()
      assertThat(mapType).isNull()

      assertThat(isArrayType).isFalse()
      assertThat(isBooleanType).isFalse()
      assertThat(isBytesType).isFalse()
      assertThat(isDoubleType).isFalse()
      assertThat(isEnumType).isFalse()
      assertThat(isError).isFalse()
      assertThat(isFloatType).isFalse()
      assertThat(isIntType).isFalse()
      assertThat(isLongType).isFalse()
      assertThat(isMapType).isFalse()
      assertThat(isNullType).isFalse()
      assertThat(isNullable).isFalse()
      assertThat(isPrimitive).isTrue()
      assertThat(isRecordType).isFalse()
      assertThat(isStringType).isTrue()
      assertThat(isUnion).isFalse()
      assertThat(isUnionType).isFalse()
    }
  }

}
