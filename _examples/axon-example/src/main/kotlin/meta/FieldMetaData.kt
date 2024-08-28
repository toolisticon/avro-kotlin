package io.holixon.axon.avro.generator.meta

import com.fasterxml.jackson.databind.ObjectMapper
import io.holixon.axon.avro.generator.meta.FieldMetaData.Companion.KEYS.TYPE
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.avro.value.Name
import org.apache.avro.Schema
import kotlin.collections.filter
import kotlin.collections.get
import kotlin.collections.map

data class FieldMetaData(
  val name: Name,
  val type: FieldMetaDataType?
) {
  companion object {
    object KEYS {
      const val NAME = "name"
      const val TYPE = "type"
    }

    fun RecordField.fieldMetaData(): FieldMetaData? = this.properties.getMeta {

      FieldMetaData(
        name = this@fieldMetaData.name,
        type = this[TYPE]?.let{ it as String }?.let { FieldMetaDataType[it.trim()] }
      )
    }
  }
}
