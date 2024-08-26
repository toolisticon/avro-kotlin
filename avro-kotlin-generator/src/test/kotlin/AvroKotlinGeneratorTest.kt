package io.toolisticon.kotlin.avro.generator

import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.builder.AvroBuilder
import io.toolisticon.kotlin.avro.generator.TestFixtures.DEFAULT_GENERATOR
import io.toolisticon.kotlin.avro.generator.TestFixtures.DEFAULT_PROPERTIES
import io.toolisticon.kotlin.avro.generator.TestFixtures.expectedSource
import io.toolisticon.kotlin.avro.generator.TestFixtures.parseDeclaration
import io.toolisticon.kotlin.avro.logical.BuiltInLogicalType
import io.toolisticon.kotlin.avro.logical.StringLogicalType
import io.toolisticon.kotlin.avro.model.SchemaType
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class AvroKotlinGeneratorTest {

    private val generator = AvroKotlinGenerator(
        properties = DEFAULT_PROPERTIES.copy(schemaTypeSuffix = "Data"),
    )


    @Test
    fun `generate simple data class for schema`() {
        val declaration = AvroParser().parseSchema(
            SchemaBuilder.record("a.b.c.Dee")
                .doc("This is the Dee message.")
                .fields()
                .name("x")
                .doc("this is x")
                .type(
                    AvroBuilder.primitiveSchema(
                        SchemaType.STRING,
                        object : StringLogicalType(BuiltInLogicalType.UUID.logicalTypeName) {}).get()
                )
                .noDefault()
                .endRecord()
        )

        val file = generator.generate(declaration).single()

        assertThat(file.code).isEqualToIgnoringWhitespace(
            """
      package a.b.c

      import jakarta.`annotation`.Generated
      import java.util.UUID
      import kotlinx.serialization.Contextual
      import kotlinx.serialization.SerialName
      import kotlinx.serialization.Serializable

      /**
       * This is the Dee message.
       *
       * @param x this is x
       */
      @Generated(value = ["io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator"], date = "2024-08-21T23:19:02.152209Z")
      @Serializable
      @SerialName(value = "a.b.c.Dee")
      public data class DeeData(
        /**
         * this is x
         */
        @Contextual
        public val x: UUID,
      )
    """.trimIndent()
        )
    }

    @Test
    fun `simple nested data class`() {
        val declaration = parseDeclaration("schema/SingleNestedRecord.avsc")

        val file = generator.generate(declaration).single()
        println(file.code)

        assertThat(file.code).isEqualToIgnoringWhitespace(
            """
      package io.acme.schema

      import jakarta.`annotation`.Generated
      import kotlin.Boolean
      import kotlinx.serialization.SerialName
      import kotlinx.serialization.Serializable

      /**
       * This is a record with a nested subtype.
       *
       * @param complexValue The nested value.
       */
      @Generated(value = ["io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator"], date =
          "2024-08-21T23:19:02.152209Z")
      @Serializable
      @SerialName(value = "io.acme.schema.SingleNestedRecord")
      public data class SingleNestedRecordData(
        /**
         * The nested value.
         */
        public val complexValue: NestedRecordData,
      ) {
        /**
         * The nested record
         */
        @Serializable
        @SerialName(value = "io.acme.schema.NestedRecord")
        public data class NestedRecordData(
          public val x: Boolean,
        )
      }
    """.trimIndent()
        )
    }

    @Test
    fun `generate SchemaContainingEnum`() {
        val declaration = parseDeclaration("schema/SchemaContainingEnum.avsc")
        val file = DEFAULT_GENERATOR.generate(declaration).single()

        assertThat(file.code).isEqualToIgnoringWhitespace(expectedSource(file.className))
    }
}
