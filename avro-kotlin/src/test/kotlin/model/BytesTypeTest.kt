package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder.primitiveSchema
import io.toolisticon.kotlin.avro.model.SchemaType.STRING
import io.toolisticon.kotlin.avro.model.SchemaType.BYTES
import io.toolisticon.kotlin.avro.value.ObjectProperties
import org.apache.avro.LogicalTypes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BytesTypeTest {

  @Test
  fun `verify to string`() {
    val schema = primitiveSchema(
      type = STRING,
      logicalType = LogicalTypes.uuid(),
      properties = ObjectProperties("foo" to 5)
    )
    val type = StringType(schema)

    assertThat(type).hasToString("StringType(logicalType='uuid', properties={foo=5})")
  }

  @Test
  fun `bytesType jsonString`() {
    val json = primitiveSchema(BYTES).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "bytes"
      }
    """.trimIndent()
    )
  }
}
