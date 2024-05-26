package io.toolisticon.kotlin.avro.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions

/**
 * Reflective access to [KSerializer<*>] for a given type.
 *
 * The type has to be a data class or enum and annotated with [Serializable].
 *
 * @param type the class to access the serializer on
 * @return kserializer of given type
 * @throws IllegalArgumentException when type is not a valid [Serializable] type
 */
@Throws(IllegalArgumentException::class)
fun KClass<*>.kserializer(): KSerializer<*> {
  require(this.isData) { "Type ${this.qualifiedName} is not a data class." }
  require(this.annotations.any { it is Serializable }) { "Type ${this.qualifiedName} is not serializable." }

  val serializerFn = requireNotNull(
    this.companionObject?.functions?.find { it.name == "serializer" }
  ) { "Type ${this.qualifiedName} must have a Companion.serializer, as created by the serialization compiler plugin." }

  return serializerFn.call(this.companionObjectInstance) as KSerializer<*>
}
