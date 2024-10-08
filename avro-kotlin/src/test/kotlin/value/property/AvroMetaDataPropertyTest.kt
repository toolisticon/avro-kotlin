package io.toolisticon.kotlin.avro.value.property

import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.isNull

class AvroMetaDataPropertyTest {

  data class MetaFoo(
    val a: Int,
    val b: String,
    val c: Long? = null,
  ) : AvroMetaData


  val extractor: ObjectProperties.() -> MetaFoo? = {
    if (isNotEmpty()) {
      val a: Int? = getValueOrNull("a")
      val b: String? = getValueOrNull("b")
      val c: Long? = getValueOrNull("c")

      MetaFoo(a = a!!, b = b!!, c = c)
    } else {
      null
    }
  }

  @Test
  fun `extract from properties`() {

    val properties = ObjectProperties(
      mapOf(
        AvroMetaDataProperty.PROPERTY_KEY to mapOf(
          "a" to 2,
          "b" to "foo"
        )
      )
    )
    val metaProperties = AvroMetaDataProperty.from(properties)

    val meta: MetaFoo? = metaProperties.metaData(extractor)

    assertThat(meta).isNotNull
    assertThat(meta).isEqualTo(MetaFoo(a = 2, b = "foo"))
  }

  @Test
  fun `empty if meta is not present`() {

    val metaProperties = AvroMetaDataProperty.from(ObjectProperties())

    assertThat(metaProperties.metaData(extractor)).isNull()
  }

  @Test
  fun `extract from schema`() {

    data class SchemaMeta(
      val fqn: CanonicalName,
      val a: Int
    ) : AvroMetaData

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

    val meta: SchemaMeta? = schema.metaData {
      SchemaMeta(
        fqn = this.canonicalName,
        a = this.properties.meta.getValue<Int>("a")
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

