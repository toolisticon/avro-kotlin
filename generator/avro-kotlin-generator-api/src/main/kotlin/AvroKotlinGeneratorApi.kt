package io.toolisticon.kotlin.avro.generator.api

import com.github.avrokotlin.avro4k.AvroDecimal
import com.github.avrokotlin.avro4k.serializer.AvroSerializer
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import io.toolisticon.kotlin.avro.declaration.AvroDeclaration
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildAnnotation
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpec
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpecSupplier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

object AvroKotlinGeneratorApi {

  data class SerializableWithAnnotation(val serializerClass: Avro4kSerializerKClass) : KotlinAnnotationSpecSupplier {
    override fun spec(): KotlinAnnotationSpec = buildAnnotation(Serializable::class) {
      addKClassMember("with", serializerClass)
    }
  }

  data class ScalePrecisionAnnotation(val precision: Int = 0, val scale: Int = 0) : KotlinAnnotationSpecSupplier {
    override fun spec(): KotlinAnnotationSpec = buildAnnotation(AvroDecimal::class) {
      addNumberMember("precision", precision)
      addNumberMember("scale", scale)
    }
  }

  data class AvroNameAnnotation(val name: String) : KotlinAnnotationSpecSupplier {
    override fun spec(): KotlinAnnotationSpec = buildAnnotation(SerialName::class) {
      addStringMember("value", name)
    }
  }

  fun rootClassName(avroDeclaration: AvroDeclaration, properties: AvroKotlinGeneratorProperties? = null) = avroClassName(
    namespace = avroDeclaration.namespace,
    name = avroDeclaration.name,
    properties = properties
  )

  fun avroClassName(namespace: Namespace, name: Name, properties: AvroKotlinGeneratorProperties? = null) = ClassName(
    packageName = namespace.value,
    simpleNames = listOf((name.suffix(properties?.schemaTypeSuffix)).value)
  )

  fun avroClassName(namedType: AvroNamedType, properties: AvroKotlinGeneratorProperties? = null): ClassName = avroClassName(
    namespace = namedType.namespace ?: Namespace(""),
    name = namedType.name,
    properties = properties
  )

  /**
   * Generates KDoc documentation.
   * @return code block.
   */
  fun AvroNamedType.kdoc(): CodeBlock = buildCodeBlock {
    addStatement("%L", this@kdoc.documentation?.value ?: this@kdoc.name.value)

    when (this@kdoc) {
      is RecordType -> {
        val fieldDocumentation = this@kdoc.fields.associate {
          it.name to it.documentation
        }
        addStatement("")

        fieldDocumentation.forEach { (name, doc) ->
          doc?.let {
            addStatement("@param %L - %L", name.value, it)
          }
        }
      }

      is EnumType -> {}
      is ErrorType -> TODO()
      is FixedType -> TODO()
    }
  }

  fun CanonicalName.asClassName() = ClassName(this.namespace.value, this.name.value)
}

typealias Avro4kSerializerKClass = KClass<out AvroSerializer<*>>
