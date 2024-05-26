package io.toolisticon.kotlin.avro.example.customerid

import kotlinx.serialization.Serializable

/**
 * TODO: value class does not wrk in generated apache java classes ... data class dows.
 */
@Serializable
data class CustomerIdData(
  val id: String
)
