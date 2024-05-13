package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.JsonString
import io.toolisticon.avro.kotlin.value.Name
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EmptyTypeTest {

  @Test
  fun `verify empty type singleton`() {
    assertThat(EmptyType.name).isEqualTo(Name.EMPTY)
    assertThat(EmptyType.schema.type).isEqualTo(SchemaType.RECORD)
    assertThat(EmptyType.json).isEqualTo(JsonString("""{
      |  "type" : "record",
      |  "fields" : [ ]
      |}""".trimMargin()))

    assertThat(EmptyType.toString()).isEqualTo("""EmptyType(hashCode=${EmptyType.hashCode}, fingerprint=${EmptyType.fingerprint})""")
  }
}
