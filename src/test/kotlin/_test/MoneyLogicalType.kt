package io.toolisticon.avro.kotlin._test

import io.toolisticon.avro.kotlin.logical.AvroLogicalType
import io.toolisticon.avro.kotlin.value.LogicalTypeName
import org.apache.avro.Conversion
import org.apache.avro.LogicalType
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.javamoney.moneta.Money

class MoneyLogicalType : AvroLogicalType<Money> {
  companion object {
    val INSTANCE: MoneyLogicalType by lazy {
      LogicalTypes.getCustomRegisteredTypes()[NAME] as MoneyLogicalType
    }
    const val NAME = "money"

    fun Money.toCharSequence() = "${this.numberStripped} ${this.currency.currencyCode}"
    fun CharSequence.toMoney(): Money {
      val (amount, currencyCode) = this.split(" ".toPattern(), 2)
      return Money.of(amount.toBigDecimal(), currencyCode)
    }
  }

  override val name: LogicalTypeName = LogicalTypeName(NAME)

  override val logicalType: LogicalType = object : LogicalType(NAME) {

    override fun validate(schema: Schema) {
      super.validate(schema)
      require(schema.type == Schema.Type.STRING) { "Only STRING is supported for logicalType=$NAME." }
    }
  }

  override val conversion: Conversion<Money> = object : Conversion<Money>() {
    override fun getConvertedType(): Class<Money> = Money::class.java

    override fun getLogicalTypeName(): String = NAME

    override fun getRecommendedSchema(): Schema = Schema.create(Schema.Type.STRING).apply {
      this@MoneyLogicalType.logicalType.addToSchema(this)
    }

    override fun fromCharSequence(value: CharSequence, schema: Schema, type: LogicalType): Money {
      return value.toMoney()
    }

    override fun toCharSequence(value: Money, schema: Schema, type: LogicalType): CharSequence {
      return value.toCharSequence()
    }
  }

  override fun fromSchema(schema: Schema): LogicalType? = if (schema.getProp(LogicalType.LOGICAL_TYPE_PROP) == NAME) logicalType else null
}
