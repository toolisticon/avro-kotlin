package io.toolisticon.avro.kotlin.example.money

import io.toolisticon.kotlin.avro.serialization.spi.AvroSerializerModuleFactory
import kotlinx.serialization.modules.SerializersModule
import org.javamoney.moneta.Money

class MoneySerializerModuleFactory : AvroSerializerModuleFactory {
  override fun invoke(): SerializersModule = SerializersModule {
    contextual(Money::class, MoneySerializer())
  }
}
