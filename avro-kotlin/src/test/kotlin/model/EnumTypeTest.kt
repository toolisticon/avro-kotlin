package io.toolisticon.kotlin.avro.model

import io.toolisticon.kotlin.avro.AvroKotlin.parseSchema
import io.toolisticon.kotlin.avro.value.Documentation
import io.toolisticon.kotlin.avro.value.JsonString
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledOnOs
import org.junit.jupiter.api.condition.OS

internal class EnumTypeTest {

  @Test
  @DisabledOnOs(OS.WINDOWS)
  fun `create enumType from schema`() {
    val json = JsonString.of(
      """
      {
        "type": "enum",
        "doc": "The playing cards.",
        "name": "cards.Suit",
        "default" : "HEARTS",
        "symbols" : ["SPADES", "HEARTS", "DIAMONDS", "CLUBS"]
      }
      """.trimIndent()
    )
    val schema = parseSchema(json)

    val type: EnumType = AvroType.avroType(schema)

    assertThat(type.namespace).isEqualTo(Namespace("cards"))
    assertThat(type.name).isEqualTo(Name("Suit"))
    assertThat(type.hashCode).isEqualTo(schema.hashCode)
    assertThat(type.fingerprint).isEqualTo(schema.fingerprint)
    assertThat(type.documentation).isEqualTo(Documentation("The playing cards."))
    assertThat(type.symbols).containsExactly("SPADES", "HEARTS", "DIAMONDS", "CLUBS")
    assertThat(type.defaultSymbol).isEqualTo("HEARTS")

    assertThat(type.hasEnumSymbol("DIAMONDS")).isTrue()
    assertThat(type.enumOrdinal("DIAMONDS")).isEqualTo(2)

    assertThat(type.hasEnumSymbol("XXX")).isFalse()
    assertThat(type.enumOrdinal("XXX")).isEqualTo(-1)

    assertThat(type.json.value).isEqualTo(
      """
      {
        "type" : "enum",
        "name" : "Suit",
        "namespace" : "cards",
        "doc" : "The playing cards.",
        "symbols" : [ "SPADES", "HEARTS", "DIAMONDS", "CLUBS" ],
        "default" : "HEARTS"
      }
    """.trimIndent()
    )
  }
}
