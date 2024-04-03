package io.toolisticon.avro.kotlin.model.wrapper

import io.toolisticon.avro.kotlin.TestFixtures
import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.model.SchemaType.STRING
import io.toolisticon.avro.kotlin.value.Name
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class AvroSchemaTest {

  @Test
  fun `no exceptions for primitive string`() {
    val schema = AvroBuilder.primitiveSchema(STRING)

    with(schema) {
      assertThat(aliases).isEmpty()
      assertThat(schema.fullName).isEqualTo("string")
      assertThat(schema.name).isEqualTo(Name("string"))
      assertThat(schema.namespace.isEmpty()).isTrue()
      assertThat(schema.type).isEqualTo(STRING)

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


  @Test
  @Disabled("Fix!")
  fun `recurse enclosed types map for json`() {
    val schema = AvroSchema(TestFixtures.ApacheAvroResourceFixtures.JSON_AVSC)
    println(schema.typesMap)
  }
}
