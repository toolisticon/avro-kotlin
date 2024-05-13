package io.toolisticon.avro.kotlin.example.money

import io.toolisticon.avro.kotlin.logical.StringSimpleLogicalType
import io.toolisticon.avro.kotlin.logical.StringSimpleLogicalTypeFactory
import io.toolisticon.avro.kotlin.logical.conversion.StringSimpleLogicalTypeConversion
import io.toolisticon.avro.kotlin.logical.conversion.TypeConverter
import io.toolisticon.avro.kotlin.value.LogicalTypeName.Companion.toLogicalTypeName
import org.javamoney.moneta.Money
import java.util.*
import javax.money.format.AmountFormatQuery
import javax.money.format.MonetaryFormats

object MoneyLogicalType : StringSimpleLogicalType("money".toLogicalTypeName()), TypeConverter<String, Money> {
  val convertedType = Money::class.java
  private val format = MonetaryFormats.getAmountFormat(AmountFormatQuery.of(Locale.GERMAN))

  class MoneyLogicalTypeFactory : StringSimpleLogicalTypeFactory<MoneyLogicalType>(logicalType = MoneyLogicalType)

  class MoneyConversion : StringSimpleLogicalTypeConversion<MoneyLogicalType, Money>(MoneyLogicalType, convertedType) {
    override fun fromAvro(value: String): Money = MoneyLogicalType.fromAvro(value)

    override fun toAvro(value: Money): String = MoneyLogicalType.toAvro(value)
  }

  override fun fromAvro(value: String): Money {
    return Money.from(format.parse(value))
  }

  override fun toAvro(value: Money): String {
    return format.format(value)
  }
}
