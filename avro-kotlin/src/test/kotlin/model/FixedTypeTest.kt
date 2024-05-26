package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.builder.AvroBuilder
import io.toolisticon.kotlin.avro.logical.BuiltInLogicalType
import io.toolisticon.kotlin.avro.value.Documentation
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace
import org.apache.avro.LogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

internal class FixedTypeTest {

  @Test
  @DisabledOnOs(OS.WINDOWS)
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
