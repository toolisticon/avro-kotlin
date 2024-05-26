package io.toolisticon.kotlin.avro.logical.conversion

import io.toolisticon.kotlin.avro.logical.conversion.parameterized.ParameterizedStringConversion
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

internal class PrimitiveTypeConversionsTest {

    class CustomParameterizedStringConversion :
        ParameterizedStringConversion<UUID>(logicalTypeName = LogicalTypeName("uuid"), convertedType = UUID::class.java) {
        override fun fromAvro(value: String, schema: AvroSchema, logicalType: LogicalType?): UUID =
            UUID.fromString(value)

        override fun toAvro(value: UUID, schema: AvroSchema, logicalType: LogicalType?): String = value.toString()
    }



    @Nested
    inner class ParameterizedStringConversionTest {

        @Test
        fun `convert uuid from to string`() {
            val stringConversion = CustomParameterizedStringConversion()
            val uuid = UUID.randomUUID()

            val string = stringConversion.toAvro(uuid)
            assertThat(stringConversion.fromAvro(string)).isEqualTo(uuid)
        }
    }

}
