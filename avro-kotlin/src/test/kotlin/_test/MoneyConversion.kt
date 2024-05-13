package io.toolisticon.avro.kotlin._test

import io.toolisticon.avro.kotlin.logical.conversion.StringConversion
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import org.javamoney.moneta.Money
import java.util.*
import javax.money.format.AmountFormatQuery
import javax.money.format.MonetaryFormats

class MoneyConversion : StringConversion<Money>(LogicalTypeName("money"), Money::class.java) {
  private val format = MonetaryFormats.getAmountFormat(AmountFormatQuery.of(Locale.GERMAN))

  override fun fromAvro(value: String, schema: AvroSchema, logicalType: LogicalType?): Money {
    return Money.from(format.parse(value))
  }

  override fun toAvro(value: Money, schema: AvroSchema, logicalType: LogicalType?): String {
    return format.format(value)
  }

  override fun getRecommendedSchema(): Schema {
    return super.getRecommendedSchema()
  }
}
