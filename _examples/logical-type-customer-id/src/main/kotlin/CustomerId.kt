package io.toolisticon.avro.kotlin.example.customerid

import kotlinx.serialization.Serializable
import java.util.*

@JvmInline
@Serializable(with = CustomerIdLogicalType.CustomerIdSerializer::class)
value class CustomerId(val id: String) {
  companion object {
    @JvmStatic
    fun random(): CustomerId = of(UUID.randomUUID().toString())

    @JvmStatic
    fun of(id: String):CustomerId = CustomerId(id)
  }
}
