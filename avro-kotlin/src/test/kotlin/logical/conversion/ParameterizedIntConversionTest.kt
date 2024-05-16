package io.toolisticon.avro.kotlin.logical.conversion

import com.ibm.icu.text.RuleBasedNumberFormat
import io.toolisticon.avro.kotlin.logical.conversion.parameterized.ParameterizedIntConversion
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class ParameterizedIntConversionTest {


  val conversion = object : ParameterizedIntConversion<String>(LogicalTypeName("number"), String::class.java) {
    private val number = RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT)

    override fun fromAvro(value: Int, schema: AvroSchema, logicalType: LogicalType?): String = number.format(value)

    override fun toAvro(value: String, schema: AvroSchema, logicalType: LogicalType?): Int =
      number.parse(value).toInt()
  }

  @Test
  fun `convert int to word and back`() {
    val word = "seventeen"

    val num = conversion.toAvro(word)

    assertThat(conversion.fromAvro(num)).isEqualTo(word)
  }
}
