package io.toolisticon.kotlin.avro.logical.conversion

import com.ibm.icu.text.RuleBasedNumberFormat
import io.toolisticon.kotlin.avro.logical.conversion.parameterized.ParameterizedIntConversion
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.LogicalTypeName
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
