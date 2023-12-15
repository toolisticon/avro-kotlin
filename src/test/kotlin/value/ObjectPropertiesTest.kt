package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema.Type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ObjectPropertiesTest {

  @Test
  fun `no props means empty map`() {
    val schema = primitiveSchema(Type.STRING)
    val props = ObjectProperties(schema)

    assertThat(props).isEmpty()
  }

  @Test
  fun `with properties and logicalType`() {
    val schema = primitiveSchema(
      type = Type.BYTES,
      properties = ObjectProperties("xxx" to "value"),
      logicalType = LogicalTypes.uuid()
    )
    val props = schema.properties

    assertThat(props).isNotEmpty
    assertThat(props).doesNotContainKey("logicalType")
    assertThat(props.getValue<String>("xxx")).isEqualTo("value")
  }

  @Test
  fun `with properties map`() {
    val innerMap = ObjectProperties(
      "a" to 5,
      "b" to "foo"
    )

    val props = ObjectProperties(
      primitiveSchema(type = Type.STRING, properties = ObjectProperties("zzz" to innerMap))
    )

    assertThat(props).isNotEmpty
    assertThat(props.getMap("zzz").getValue<Int>("a")).isEqualTo(5)

    val p = props.getValue<ObjectProperties>("zzz")

    assertThat(p.getValue<String>("b")).isEqualTo("foo")
  }
}
