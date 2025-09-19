package io.toolisticon.kotlin.avro.serialization.serializer

import _ktx.StringKtx
import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import com.github.avrokotlin.avro4k.ExperimentalAvro4kApi
import com.github.avrokotlin.avro4k.serializer.AvroSerializer
import com.github.avrokotlin.avro4k.serializer.SchemaSupplierContext
import io.toolisticon.kotlin.avro.logical.AvroLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.AvroLogicalTypeConversion
import io.toolisticon.kotlin.avro.serialization.avro4k.AvroDecoderType
import io.toolisticon.kotlin.avro.serialization.avro4k.AvroDecoderType.Companion.avroDecoderType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

@OptIn(ExperimentalSerializationApi::class, ExperimentalAvro4kApi::class)
sealed class AvroLogicalTypeSerializer<LOGICAL : AvroLogicalType<JVM_TYPE>, JVM_TYPE : Any, CONVERTED_TYPE : Any>(
  val conversion: AvroLogicalTypeConversion<LOGICAL, JVM_TYPE, CONVERTED_TYPE>,
  private val primitiveKind: PrimitiveKind,
  private val encode: AvroEncoder.(JVM_TYPE) -> Unit,
  private val decode: AvroDecoder.() -> JVM_TYPE
) : AvroSerializer<CONVERTED_TYPE>(descriptorName = conversion.convertedType.qualifiedName!!) {

  override fun serializeAvro(encoder: AvroEncoder, value: CONVERTED_TYPE) {
    encoder.encode(conversion.toAvro(value))
  }

  /**
   * This is called by avro4k/kotlinx to deserialize.
   * We use the conversion to convert to the CONVERTED_TYPE and the decode-fn to decode to the JVM_TYPE.
   *
   * TODO: generic records behave differently, the generic record might already contain the CONVERTED_TYPE
   * value if it was created using GenericData that is logicalType/conversion aware.
   * To Overcome this issue, we use the deprecated `decodeValue` and check the type to determine if we have to decode.
   * As this relies on deprecated code, we must address this, the switch is a work around.
   */
  override fun deserializeAvro(decoder: AvroDecoder): CONVERTED_TYPE = when (decoder.avroDecoderType()) {
    AvroDecoderType.DIRECT -> conversion.fromAvro(decoder.decode())
    AvroDecoderType.GENERIC -> {
      val value = requireNotNull(decoder.decodeValue()) { "Can't deserialize null" }
      @Suppress("UNCHECKED_CAST")
      when (value::class) {
        conversion.convertedType -> value as CONVERTED_TYPE
        else -> conversion.fromAvro(decoder.decode())
      }
    }
  }


  override fun getSchema(context: SchemaSupplierContext): Schema {
    return conversion.logicalType.schema().get()
  }

  override fun toString(): String = StringKtx.toString(this::class.java.simpleName) {
    add("conversion", conversion)
    add("primitiveKind", primitiveKind)
  }
}
