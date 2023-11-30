package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.AvroKotlin.parseSchema
import io.toolisticon.avro.kotlin.TestFixtures
import io.toolisticon.avro.kotlin.TestFixtures.resourceUrl
import io.toolisticon.avro.kotlin.value.Name
import org.apache.avro.Schema.Type
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroTypesMapTest {

  @Test
  fun `two types for SimpleString`() {
    val schema = parseSchema(resourceUrl("schema/SimpleStringRecord.avsc"))

    val map = AvroTypesMap(schema)

    assertThat(map).hasSize(2)
    assertThat(map.graph.sequence.toList()).hasSize(2)
  }

  @Test
  fun `buildCatalog - STRING`() {
    val string = AvroBuilder.primitiveSchema(Type.STRING)

    val map = AvroTypesMap(AvroBuilder.primitiveSchema(Type.STRING))
    assertThat(map).hasSize(1)
    assertThat(map.values.first()).isInstanceOf(StringType::class.java)
    assertThat(map.sequence().single().hashCode).isEqualTo(string.hashCode)
  }


  @Test
  fun `can create map with reflective field`() {
    // this schema contains itself as a field-type and must only result in one map entry
    val schema = AvroSchema(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .name("self")
        .type("foo.Bar")
        .noDefault()
        .endRecord()
    )

    assertThat(schema.hashCode).isEqualTo(schema.getField(Name("self"))?.schema?.hashCode)

    val map = AvroTypesMap(schema)
    assertThat(map).hasSize(1)
  }


  @Test
  fun `build types-map and graph`() {
    val schema = parseSchema(resourceUrl("schema/ReUsingTypes.avsc"))

    val map = AvroTypesMap(schema)

    assertThat(map.sequence().toList()).hasSize(4)
  }

  @Test
  fun `with nullable map`() {
    val schema = TestFixtures.RECORD_MAP_WITH_NULLABLE_UUIDS

    val m = AvroTypesMap(schema)

    assertThat(m.sequence().toList()).hasSize(5)
  }


  @Test
  fun `remove root`() {
    val schema = TestFixtures.RECORD_MAP_WITH_NULLABLE_UUIDS

    val m = AvroTypesMap(schema) - schema.hashCode

    assertThat(m).hasSize(4)
    assertThat(m.sequence().toList()).hasSize(4)

  }
}
