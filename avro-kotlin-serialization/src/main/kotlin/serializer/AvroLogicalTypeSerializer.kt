package io.toolisticon.kotlin.avro.serialization.serializer

import _ktx.StringKtx
import com.github.avrokotlin.avro4k.schema.AvroDescriptor
import com.github.avrokotlin.avro4k.schema.NamingStrategy
import com.github.avrokotlin.avro4k.serializer.AvroSerializer
import io.toolisticon.avro.kotlin.logical.AvroLogicalType
import io.toolisticon.avro.kotlin.logical.conversion.AvroLogicalTypeConversion
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule
import org.apache.avro.Schema

@OptIn(ExperimentalSerializationApi::class)
sealed class AvroLogicalTypeSerializer<LOGICAL : AvroLogicalType<JVM_TYPE>, JVM_TYPE : Any, CONVERTED_TYPE : Any>(
  val conversion: AvroLogicalTypeConversion<LOGICAL, JVM_TYPE, CONVERTED_TYPE>,
  private val primitiveKind: PrimitiveKind
) : AvroSerializer<CONVERTED_TYPE>() {

  override val descriptor: SerialDescriptor = object : AvroDescriptor(type = conversion.convertedType, kind = primitiveKind) {
    override fun schema(annos: List<Annotation>, serializersModule: SerializersModule, namingStrategy: NamingStrategy): Schema {
      return conversion.logicalType.schema().get()
    }
  }

  override fun toString(): String = StringKtx.toString(this::class.java.simpleName) {
    add("conversion", conversion)
    add("primitiveKind", primitiveKind)
  }
}
