package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.builder.AvroBuilder
import io.toolisticon.avro.kotlin.logical.BuiltInLogicalType
import io.toolisticon.avro.kotlin.value.Documentation
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace
import org.apache.avro.LogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class FixedTypeTest {

  @Test
  fun `fixedType with jsonString`() {
    val json = AvroBuilder.fixed(
      name = Namespace("foo.bar") + Name("Dummy"),
      size = 16,
      documentation = Documentation("This is fixed"),
      logicalType = LogicalType(BuiltInLogicalType.DURATION.logicalTypeName.value)
    ).json

    assertThat(json.value).isEqualTo(
      """
      {
        "type" : "fixed",
        "name" : "Dummy",
        "namespace" : "foo.bar",
        "doc" : "This is fixed",
        "size" : 16,
        "logicalType" : "duration"
      }
    """.trimIndent()
    )
  }
}
