package io.toolisticon.avro.kotlin.example.customerid

import kotlinx.serialization.Serializable
import java.util.*

@JvmInline
@Serializable(with = CustomerIdLogicalType.CustomerIdSerializer::class)
value class CustomerId(val id: String) {
  companion object {
    fun random() = CustomerId(UUID.randomUUID().toString())
  }
}
