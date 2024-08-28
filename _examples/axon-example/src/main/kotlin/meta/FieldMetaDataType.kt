package io.holixon.axon.avro.generator.meta

import java.util.*
import kotlin.collections.associateBy
import kotlin.let
import kotlin.text.lowercase
import kotlin.text.replaceFirstChar

enum class FieldMetaDataType {
  IdentifierRef,
  ;

  val decapitalizedName = name.replaceFirstChar { c -> c.lowercase(Locale.getDefault()) }


  companion object {
    private val NAMES: Map<String, FieldMetaDataType> = FieldMetaDataType.entries.associateBy { it.decapitalizedName }

    operator fun get(name: String?): FieldMetaDataType? = name?.let { NAMES[it] }
  }
}
