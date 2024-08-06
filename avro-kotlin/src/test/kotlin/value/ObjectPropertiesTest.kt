package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.BYTES
import io.toolisticon.kotlin.avro.model.SchemaType.STRING
import io.toolisticon.kotlin.avro.value.property.LogicalTypeNameProperty
import org.apache.avro.LogicalTypes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ObjectPropertiesTest {

  @Test
  fun `no props means empty map`() {
    val schema = primitiveSchema(STRING)
    val props = schema.properties

    assertThat(props).isEmpty()
  }

  @Test
  fun `with properties and logicalType`() {
    val schema = primitiveSchema(
      type = STRING,
      properties = ObjectProperties("xxx" to "value"),
      logicalType = LogicalTypes.uuid()
    )
    val props = schema.properties

    assertThat(props).isNotEmpty
    assertThat(props).containsKey(LogicalTypeNameProperty.PROPERTY_KEY)
    assertThat(props.getValue<String>("xxx")).isEqualTo("value")
  }

  @Test
  fun `with properties map`() {
    val innerMap = ObjectProperties(
      "a" to 5,
      "b" to "foo"
    )

    val props = primitiveSchema(
      type = STRING,
      properties = ObjectProperties("zzz" to innerMap)
    ).properties


    assertThat(props).isNotEmpty
    assertThat(props.getMap("zzz").getValue<Int>("a")).isEqualTo(5)

    val p = props.getValue<ObjectProperties>("zzz")

    assertThat(p.getValue<String>("b")).isEqualTo("foo")
  }

  @Test
  fun `empty properties has logicalTypeName null`() {
    assertThat(LogicalTypeNameProperty.from(ObjectProperties.EMPTY)).isNull()
  }
}
