package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import org.apache.avro.JsonProperties

/**
 * Represents properties present on [org.apache.avro.JsonProperties].
 *
 * To fulfill the restrictions of the avro json mapping,
 * value types are limited to:
 *
 *   * Map
 *   * Collection
 *   * boolean
 *   * Int
 *   * Long
 *   * Float
 *   * Double
 *   * ByteArray
 *   * String
 */
@JvmInline
value class ObjectProperties(override val value: Map<String, Any> = emptyMap()) : Map<String, Any> by value, ValueType<Map<String, Any>> {
  companion object {
    private val ignoredProperties = setOf(LogicalTypeName.PROPERTY_KEY)
    val EMPTY = ObjectProperties()
  }

  /**
   * Creates new instance from schema objectProps.
   *
   * @param avroSchema the wrapped schema
   * @return properties derived from schema
   */
  constructor(avroSchema: AvroSchema) : this(avroSchema.get())

  /**
   * Creates new instance from schema objectProps.
   *
   * @param jsonProperties schema, field or protocol containing objectProps
   * @return properties derived from jsonProperties
   */
  constructor(jsonProperties: JsonProperties) : this(value = jsonProperties.objectProps.toMap())

  /**
   * Creates new instance from properties.
   *
   * @param pairs key/value pairs with properties
   * @return properties created from pairs
   */
  constructor(vararg pairs: Pair<String, Any>) : this(value = mapOf(*pairs))

  /**
   * Type-safe access to  value for given key.
   *
   * @param key the property key
   * @param <V> the expected value
   * @throws IllegalArgumentException if key does not exist
   * @return typed value of property
   */
  @Throws(IllegalArgumentException::class)
  @Suppress("UNCHECKED_CAST")
  inline fun <reified V : Any> getValue(key: String): V {
    require(contains(key)) { "unknown key: $key" }

    // This cannot be null anymore, the key exists and the value must not be null.
    val value = get(key)!!

    // if it is a map, return ObjectProperties, else cast
    return if (V::class == ObjectProperties::class) {
      ObjectProperties(value as Map<String, Any>) as V
    } else {
      value as V
    }
  }

  /**
   * Gets the value of given key as [ObjectProperties].
   *
   * @param key the property key
   * @return objectProperties of given key
   */
  fun getMap(key: String): ObjectProperties = getValue(key)

  fun logicalTypeName(): LogicalTypeName? = LogicalTypeName(this)

  /**
   * Creates a copy of this properties instance with all ignored property keys removed.
   */
  fun filterIgnoredKeys(): ObjectProperties = ObjectProperties(value = value.filterNot { ignoredProperties.contains(it.key) })

  override fun toString() = value.toString()
}
