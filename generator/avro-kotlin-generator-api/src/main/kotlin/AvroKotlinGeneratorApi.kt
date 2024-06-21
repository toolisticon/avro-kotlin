package io.toolisticon.kotlin.avro.generator.api

import com.github.avrokotlin.avro4k.serializer.AvroSerializer
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.buildCodeBlock
import io.toolisticon.kotlin.avro.declaration.AvroDeclaration
import io.toolisticon.kotlin.avro.model.AvroNamedType
import io.toolisticon.kotlin.avro.model.EnumType
import io.toolisticon.kotlin.avro.model.FixedType
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.model.ErrorType
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.annotationBuilder
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

object AvroKotlinGeneratorApi {

  fun serializableAnnotation(serializerClass: Avro4kSerializerKClass) = annotationBuilder(
    type = Serializable::class
  ).addMember("with = %T::class", serializerClass)
    .build()

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

}

typealias Avro4kSerializerKClass = KClass<out AvroSerializer<*>>
