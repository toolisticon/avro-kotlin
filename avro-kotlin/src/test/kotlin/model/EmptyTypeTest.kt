package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.value.JsonString
import io.toolisticon.kotlin.avro.value.Name
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

internal class EmptyTypeTest {

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `verify empty type singleton`() {

    assertThat(EmptyType.name).isEqualTo(Name.EMPTY)
    assertThat(EmptyType.schema.type).isEqualTo(SchemaType.RECORD)
    assertThat(EmptyType.json).isEqualTo(JsonString.of("""{
      |  "type" : "record",
      |  "fields" : [ ]
      |}""".trimMargin()))

    assertThat(EmptyType.toString()).isEqualTo("""EmptyType(hashCode=${EmptyType.hashCode}, fingerprint=${EmptyType.fingerprint})""")
  }
}
