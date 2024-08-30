package io.holixon.axon.avro.generator.meta

import io.holixon.axon.avro.generator.meta.FieldMetaData.Companion.KEYS.TYPE
import io.toolisticon.kotlin.avro.model.wrapper.AvroProtocol
import io.toolisticon.kotlin.avro.value.Name

/**
 * Meta data for Avro Axon protocol message.
 */
data class MessageMetaData(
  val name: Name,
  val type: MessageMetaDataType?
) {
  companion object {
    object KEYS {
      const val NAME = "name"
      const val TYPE = "type"
    }

    fun AvroProtocol.Message.fieldMetaData(): MessageMetaData? = this.properties.getMeta {

      MessageMetaData(
        name = this@fieldMetaData.name,
        type = this[TYPE]?.let { it as String }?.let { MessageMetaDataType[it.trim()] }
      )
    }
  }
}
