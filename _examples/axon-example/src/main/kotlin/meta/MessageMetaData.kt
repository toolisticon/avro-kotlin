package io.holixon.axon.avro.generator.meta

import io.holixon.axon.avro.generator.meta.FieldMetaData.Companion.KEYS.TYPE
import io.holixon.axon.avro.generator.meta.MessageMetaData.Companion.KEYS.GROUP
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.avro.value.Name

/**
 * Meta data for Avro Axon protocol message.
 */
data class MessageMetaData(
  val group: Name?,
  val type: MessageMetaDataType?
) {
  companion object {
    object KEYS {
      const val GROUP = "group"
      const val TYPE = "type"
    }

    /**
     * Retrieves message meta data, if any.
     */
    fun AvroProtocol.Message.messageMetaData(): MessageMetaData? = this.properties.getMeta {
      MessageMetaData(
        group = this[GROUP]?.let { it as String }?.let { Name(it.trim()) },
        type = this[TYPE]?.let { it as String }?.let { MessageMetaDataType[it.trim()] }
      )
    }
  }
}
