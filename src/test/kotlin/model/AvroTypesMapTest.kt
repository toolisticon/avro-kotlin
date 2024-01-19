package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.TestFixtures
import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.Name
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class AvroTypesMapTest {

  @Test
  fun `a reused definition can be addresses by its key`() {
    val type = SchemaBuilder.record("Sub")
      .fields()
      .requiredBoolean("x")
      .requiredString("y")
      .endRecord()

    val type2 = SchemaBuilder.record("Value")
      .doc("A map of Subs.")
      .fields()
      .requiredBoolean("z")
      .name("t")
      .type(Schema.createMap(type))
      .noDefault()
      .endRecord()

    val schema = SchemaBuilder.builder("foo")
      .record("Bar")
      .doc("This is the root Bar type.")
      .fields()
      .name("first").type(type)
      .noDefault()
      .name("second")
      .doc("The second field")
      .type(
        Schema.createUnion(
          AvroBuilder.primitiveSchema(SchemaType.NULL).get(), type
        )
      )
      .noDefault()
      .name("listOfMaps")
      .type(Schema.createArray(type2))
      .noDefault()
      .endRecord()

    val map = AvroTypesMap(AvroSchema(schema))

    assertThat(map).hasSize(9)
    assertThat(map.findTypes<RecordType>()).hasSize(3)
    assertThat(map.findTypes<RecordType>()).hasSize(3)
    assertThat(map.findTypes<UnionType>()).hasSize(1)
    assertThat(map.findTypes<BooleanType>()).hasSize(1)
    assertThat(map.findTypes<StringType>()).hasSize(1)
    assertThat(map.findTypes<NullType>()).hasSize(1)
    assertThat(map.findTypes<ArrayType>()).hasSize(1)
    assertThat(map.findTypes<MapType>()).hasSize(1)
  }

  @Test
  fun `two types for SimpleString`() {
    val schema = AvroKotlin.parseSchema("schema/SimpleStringRecord.avsc")

    val map = AvroTypesMap(schema)

    assertThat(map).hasSize(2)
    assertThat(map.graph.sequence.toList()).hasSize(2)
  }

  @Test
  fun `buildCatalog - STRING`() {
    val string = AvroBuilder.primitiveSchema(SchemaType.STRING)

    val map = AvroTypesMap(AvroBuilder.primitiveSchema(SchemaType.STRING))
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
    val schema = AvroKotlin.parseSchema("schema/ReUsingTypes.avsc")

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
