package io.toolisticon.kotlin.avro.example

import com.github.avrokotlin.avro4k.serializer.UUIDSerializer
import io.toolisticon.kotlin.avro.example.customerid.CustomerId
import io.toolisticon.kotlin.avro.example.customerid.CustomerIdLogicalType
import io.toolisticon.kotlin.avro.example.money.MoneyLogicalType.MoneySerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.javamoney.moneta.Money
import java.util.*

@Serializable
@SerialName("io.toolisticon.bank.BankAccountCreated")
data class BankAccountCreatedData(

  @Serializable(with = UUIDSerializer::class)
  val accountId: UUID,

  @Serializable(with = CustomerIdLogicalType.CustomerIdSerializer::class)
  val customerId: CustomerId,

  @Serializable(with = MoneySerializer::class)
  val initialBalance: Money,
)
