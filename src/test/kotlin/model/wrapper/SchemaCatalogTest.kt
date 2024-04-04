package io.toolisticon.avro.kotlin.model.wrapper

import io.toolisticon.avro.kotlin.TestFixtures
import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.SchemaType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class SchemaCatalogTest {


  @Test
  fun `create from primitive string`() {
    val schema = primitiveSchema(SchemaType.STRING)

    val catalog = SchemaCatalog(schema)

    assertThat(catalog).hasSize(1)
    assertThat(catalog.graph).hasSize(1)
  }

  @Test
  fun `catalog of json_avsc - complex self reference in map and array`() {
    val catalog = SchemaCatalog(AvroSchema(TestFixtures.ApacheAvroResourceFixtures.JSON_AVSC))
    assertThat(catalog).hasSize(9)

    assertThat(catalog.graph)

    val unionHash = catalog.single(SchemaType.UNION).first
    val mapHash = catalog.single(SchemaType.MAP).first
    val recordHash = catalog.single(SchemaType.RECORD).first

    assertThat(catalog.graph.contains(mapHash to recordHash)).isTrue()
    assertThat(catalog.graph.contains(unionHash to mapHash)).isTrue()
  }

  @Test
  fun `catalog of json_avsc - remove map`() {
    var catalog = SchemaCatalog(AvroSchema(TestFixtures.ApacheAvroResourceFixtures.JSON_AVSC))
    assertThat(catalog).hasSize(9)
    val mapHash = catalog.single(SchemaType.MAP).first

    catalog -= mapHash
    assertThat(catalog).hasSize(8)
    val unionHash = catalog.single(SchemaType.UNION).first

    assertThat(catalog.graph.contains(unionHash to mapHash)).isFalse()
  }

  @Test
  fun `create without self reference`() {
    val schema = AvroBuilder.union(primitiveSchema(SchemaType.NULL), primitiveSchema(SchemaType.STRING))

    assertThat(SchemaCatalog(schema = schema, excludeSelf = false)).hasSize(3)
    assertThat(SchemaCatalog(schema = schema, excludeSelf = true)).hasSize(2)

  }
}
