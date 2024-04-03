package io.toolisticon.avro.kotlin.model.support

import io.toolisticon.avro.kotlin.TestFixtures
import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.support.NamedSchema.Companion.namedSchema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class SchemaCatalogTest {


  @Test
  fun `create from primitive string`() {
    val schema = AvroBuilder.primitiveSchema(SchemaType.STRING).namedSchema

    val catalog = SchemaCatalog(schema)

    assertThat(catalog).hasSize(1)
    assertThat(catalog.graph).hasSize(1)
  }

  @Test
  fun `catalog of json_avsc - complex self reference in map and array`() {
    val catalog = SchemaCatalog(TestFixtures.ApacheAvroResourceFixtures.JSON_AVSC.namedSchema)
    assertThat(catalog).hasSize(9)

    assertThat(catalog.graph)

    val unionHash = catalog.single(SchemaType.UNION).first
    val mapHash = catalog.single(SchemaType.MAP).first
    val recordHash = catalog.single(SchemaType.RECORD).first

    assertThat(catalog.graph.contains(mapHash to recordHash))
    assertThat(catalog.graph.contains(unionHash to mapHash))
  }

}
