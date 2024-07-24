package io.toolisticon.kotlin.avro.serialization.serializer

import _ktx.StringKtx
import com.github.avrokotlin.avro4k.AvroDecoder
import com.github.avrokotlin.avro4k.AvroEncoder
import com.github.avrokotlin.avro4k.serializer.AvroSerializer
import com.github.avrokotlin.avro4k.serializer.SchemaSupplierContext
import io.toolisticon.kotlin.avro.logical.AvroLogicalType
import io.toolisticon.kotlin.avro.logical.conversion.AvroLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import org.apache.avro.Schema

@OptIn(ExperimentalSerializationApi::class)
sealed class AvroLogicalTypeSerializer<LOGICAL : AvroLogicalType<JVM_TYPE>, JVM_TYPE : Any, CONVERTED_TYPE : Any>(
  val conversion: AvroLogicalTypeConversion<LOGICAL, JVM_TYPE, CONVERTED_TYPE>,
  private val primitiveKind: PrimitiveKind
) : AvroSerializer<CONVERTED_TYPE>(descriptorName = conversion.convertedType.qualifiedName!!) {

  abstract override fun deserializeAvro(decoder: AvroDecoder): CONVERTED_TYPE

  abstract override fun serializeAvro(encoder: AvroEncoder, value: CONVERTED_TYPE)

  override fun getSchema(context: SchemaSupplierContext): Schema {
    return conversion.logicalType.schema().get()
  }

  override fun toString(): String = StringKtx.toString(this::class.java.simpleName) {
    add("conversion", conversion)
    add("primitiveKind", primitiveKind)
  }
}
