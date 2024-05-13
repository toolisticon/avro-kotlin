package io.toolisticon.avro.kotlin.logical.conversion

import com.ibm.icu.text.RuleBasedNumberFormat
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import java.util.*

internal class LongConversionTest {

  val conversion = object : LongConversion<String>(LogicalTypeName("number"), String::class.java) {
    private val numberFormat = RuleBasedNumberFormat(Locale.ENGLISH, RuleBasedNumberFormat.SPELLOUT)

    override fun fromAvro(value: Long, schema: AvroSchema, logicalType: LogicalType?): String = numberFormat.format(value)

    override fun toAvro(value: String, schema: AvroSchema, logicalType: LogicalType?): Long = numberFormat.parse(value).toLong()
  }

}
