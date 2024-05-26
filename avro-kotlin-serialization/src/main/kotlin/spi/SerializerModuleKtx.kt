package io.toolisticon.kotlin.avro.serialization.spi

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

data object SerializerModuleKtx {
  fun List<SerializersModule>.reduce() = this.fold(EmptySerializersModule(), SerializersModule::plus)
}
