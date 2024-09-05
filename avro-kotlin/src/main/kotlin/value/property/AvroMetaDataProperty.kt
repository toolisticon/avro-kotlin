package io.toolisticon.kotlin.avro.value.property

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.value.ObjectProperties
import io.toolisticon.kotlin.avro.value.ValueType

@JvmInline
value class AvroMetaDataProperty(override val value: ObjectProperties) : ValueType<ObjectProperties>, AvroProperty {
  companion object : AvroPropertySupplier<AvroMetaDataProperty> {
    const val PROPERTY_KEY = "meta"

    override fun from(properties: ObjectProperties): AvroMetaDataProperty {
      val metaProperties = if (properties.containsKey(PROPERTY_KEY)) {
        properties.getMap(AvroKotlin.META_PROPERTY)
      } else {
        ObjectProperties.EMPTY
      }
      return AvroMetaDataProperty(metaProperties)
    }
  }

  /**
   * Extract typed Meta properties.
   */
  inline fun <reified META : AvroMetaData> metaData(extractor: ObjectProperties.() -> META?): META? = value.extractor()

  /**
   * Type-safe access to  value for given key.
   */
  @Throws(IllegalArgumentException::class)
  inline fun <reified V : Any> getValue(key: String): V  = value.getValue(key)

  @Throws(IllegalArgumentException::class)
  inline fun <reified V : Any> getValueOrNull(key: String): V? = value.getValueOrNull(key)

  override val key get() = PROPERTY_KEY
}
