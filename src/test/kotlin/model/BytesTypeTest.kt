package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.value.ObjectProperties
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BytesTypeTest {

  @Test
  fun `verify to string`() {
    val schema = AvroBuilder.primitiveSchema(
      type = Schema.Type.BYTES,
      logicalType = LogicalTypes.uuid(),
      properties = ObjectProperties("foo" to 5)
    )
    val type = BytesType(schema)

    assertThat(type).hasToString("BytesType(logicalType='uuid', properties={foo=5})")
  }
}
