package io.toolisticon.avro.kotlin.model

import org.apache.avro.Protocol
import java.util.function.Supplier

class AvroProtocol(
  private val protocol: Protocol
) : Supplier<Protocol> {
  override fun get() = protocol
}
