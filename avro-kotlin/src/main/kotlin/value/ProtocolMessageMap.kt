package io.toolisticon.kotlin.avro.value

import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol.Message
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol.OneWayMessage
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol.TwoWayMessage
import org.apache.avro.Protocol

/**
 * Holds
 */
@JvmInline
value class ProtocolMessageMap<T : Message>(private val value: Map<Name, T> = emptyMap()) : Map<Name, T> by value {
  companion object {
    fun of(protocol: Protocol) = ProtocolMessageMap<Message>(
      value = buildMap {
        protocol.messages.values.map {
          if (it.isOneWay) {
            OneWayMessage(it)
          } else {
            TwoWayMessage(it)
          }
        }.forEach {
          put(it.name, it)
        }
      })
  }

  fun filterOneWay(): OneWayMessageMap = ProtocolMessageMap<OneWayMessage>(
    value = value.values.filterIsInstance<OneWayMessage>().associateBy { it.name }
  )

  fun filterTwoWay(): TwoWayMessageMap = ProtocolMessageMap<TwoWayMessage>(
    value = value.values.filterIsInstance<TwoWayMessage>().associateBy { it.name }
  )
}

/**
 * Alias for message map containing only one way.
 */
typealias OneWayMessageMap = ProtocolMessageMap<OneWayMessage>

/**
 * Alias for message map containing only two way.
 */
typealias TwoWayMessageMap = ProtocolMessageMap<TwoWayMessage>
