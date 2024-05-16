package io.toolisticon.avro.kotlin.logical.conversion

/**
 * Bidirectional converter for a given converted type and its avro primitive type converter.
 *
 * @param <REPRESENTATION> How the value is represented in a serialized avro message.
 * @param <CONVERTED_TYPE> How the value is used when message is deserialized to instance.
 */
interface TypeConverter<JVM_TYPE, CONVERTED_TYPE> {

  fun fromAvro(value: JVM_TYPE) : CONVERTED_TYPE

  fun toAvro(value: CONVERTED_TYPE) : JVM_TYPE
}
