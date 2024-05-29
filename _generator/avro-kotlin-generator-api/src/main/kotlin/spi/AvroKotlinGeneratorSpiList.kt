package io.toolisticon.kotlin.avro.generator.api.spi

import kotlin.reflect.KClass

@JvmInline
value class AvroKotlinGeneratorSpiList(private val list: List<AvroKotlinGeneratorSpi>) {

  fun <T : AvroKotlinGeneratorSpi> filterIsInstance(type: KClass<T>): List<T> = list.filterIsInstance(type.java)
}
