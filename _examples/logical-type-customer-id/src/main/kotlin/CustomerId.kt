package io.toolisticon.kotlin.avro.example.customerid

import io.toolisticon.kotlin.avro.example.customerid.CustomerIdLogicalType.CustomerIdSerializer
import kotlinx.serialization.Serializable
import java.util.*

@JvmInline
@Serializable(with = CustomerIdSerializer::class)
value class CustomerId(val id: String) {
  companion object {
    @JvmStatic
    fun random(): CustomerId = of(UUID.randomUUID().toString())

    @JvmStatic
    fun of(id: String): CustomerId = CustomerId(id)
  }

  override fun toString() = id
}
