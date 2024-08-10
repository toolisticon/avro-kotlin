package io.toolisticon.kotlin.avro.example

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.javamoney.moneta.Money

@Serializable
data class SimpleTypeWithMoney(
  @Contextual val money: Money
){}
