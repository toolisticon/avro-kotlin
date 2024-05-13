package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.SchemaType.STRING
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MapTypeTest {

  @Test
  fun `verify toString`() {
    val schema = AvroBuilder.map(primitiveSchema(STRING))
    val type = MapType(schema)

    assertThat(type).hasToString("MapType(type=string)")
  }


  @Test
  fun `type map  contains string and null`() {
    val schema = AvroBuilder.map(AvroBuilder.union(primitiveSchema(SchemaType.NULL), primitiveSchema(STRING)))
    val map: MapType = AvroType.avroType(schema)

    assertThat(map.typesMap).hasSize(3)

    assertThat(map.typesMap.values.map { it.schema.type }).containsExactlyInAnyOrder(SchemaType.UNION, SchemaType.NULL, STRING)
  }
}
