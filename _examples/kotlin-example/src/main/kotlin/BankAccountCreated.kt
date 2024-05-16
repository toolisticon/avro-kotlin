package io.toolisticon.avro.kotlin.example

import com.github.avrokotlin.avro4k.serializer.UUIDSerializer
import io.toolisticon.avro.kotlin.example.customerid.CustomerId
import io.toolisticon.avro.kotlin.example.money.MoneyLogicalType.MoneySerializer
import kotlinx.serialization.Serializable
import org.javamoney.moneta.Money
import java.util.*

@Serializable
data class BankAccountCreated(

  @Serializable(with = UUIDSerializer::class)
  val accountId: UUID,

  val customerId: CustomerId,

  @Serializable(with = MoneySerializer::class)
  val initialBalance: Money,
)
