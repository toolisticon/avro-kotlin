package io.toolisticon.kotlin.avro._test

import io.toolisticon.kotlin.avro.logical.conversion.parameterized.ParameterizedStringConversion
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.avro.value.LogicalTypeName
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import org.javamoney.moneta.Money
import java.util.*
import javax.money.format.AmountFormatQuery
import javax.money.format.MonetaryFormats

class MoneyConversionParameterized : ParameterizedStringConversion<Money>(LogicalTypeName("money"), Money::class.java) {
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
