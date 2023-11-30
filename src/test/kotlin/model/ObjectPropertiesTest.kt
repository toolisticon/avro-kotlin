package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.AvroKotlin.primitiveSchema
import org.apache.avro.Schema.Type
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ObjectPropertiesTest {

  @Test
  fun `no props means empty map`() {
    val schema = AvroSchema(primitiveSchema(Type.STRING).schema.apply {

    })
    val props = ObjectProperties(schema)

    assertThat(props).isEmpty()
  }

  @Test
  fun `with properties`() {
    val schema = AvroSchema(primitiveSchema(Type.STRING).schema.apply {
      addProp("xxx", "value")
    })
    val props = ObjectProperties(schema)


    assertThat(props).isNotEmpty
    assertThat(props.getValue<String>("xxx")).isEqualTo("value")
  }

  @Test
  fun `with properties map`() {
    val innerMap = ObjectProperties(
      "a" to 5,
      "b" to "foo"
    )

    val props = ObjectProperties(
      primitiveSchema(Type.STRING, ObjectProperties("zzz" to innerMap))
    )

    assertThat(props).isNotEmpty
    assertThat(props.getMap("zzz").getValue<Int>("a")).isEqualTo(5)

    val p = props.getValue<ObjectProperties>("zzz")

    assertThat(p.getValue<String>("b")).isEqualTo("foo")
  }
}
