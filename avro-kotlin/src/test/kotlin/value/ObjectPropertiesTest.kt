package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.STRING
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.property.LogicalTypeNameProperty
import org.apache.avro.LogicalTypes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
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

  internal data class MetaFoo(
    val a: Int,
    val b: String,
    val c: Long? = null,
  )

  @Nested
  inner class ExtractMetaTest {

    val extracor: ObjectProperties.() -> MetaFoo? = {
      val x = this
      val a: Int? = getValueOrNull("a")
      val b: String? = getValueOrNull("b")
      val c: Long? = getValueOrNull("c")

      MetaFoo(a = a!!, b = b!!, c = c)
    }

    @Test
    fun `extract from properties`() {

      val properties = ObjectProperties(
        mapOf(
          AvroKotlin.META_PROPERTY to mapOf(
            "a" to 2,
            "b" to "foo"
          )
        )
      )

      val meta: MetaFoo? = properties.getMeta(extracor)

      assertThat(meta).isNotNull
      assertThat(meta).isEqualTo(MetaFoo(a = 2, b = "foo"))
    }

    @Test
    fun `null if meta is not present`() {

      data class MetaFoo(
        val a: Int,
        val b: String,
        val c: Long? = null,
      )

      val properties = ObjectProperties()

      assertThat(properties.getMeta(extracor)).isNull()
    }

    @Test
    fun `extract from schema`() {

      data class SchemaMeta(
        val fqn: CanonicalName,
        val a: Int
      )

      val schema = AvroSchema.of(
        JsonString.of(
          """
        { "namespace": "foo", "name": "Bar",
          "type": "record",
          "meta": {
            "a": 5
          },
          "fields": [{
            "name":"a",
            "type":"string"
          }
          ]
         }

      """.trimIndent()
        )
      )

      println(schema.json)

      val meta: SchemaMeta? = schema.getMeta {

        SchemaMeta(
          fqn = this.canonicalName,
          a = this.properties.getMeta()?.getValue("a") as Int
        )
      }

      assertThat(meta).isNotNull.isEqualTo(
        SchemaMeta(
          fqn = CanonicalName(Namespace("foo"), Name("Bar")),
          a = 5
        )
      )
    }
  }
}
