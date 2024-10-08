package io.toolisticon.kotlin.avro.generator.poet

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import io.toolisticon.kotlin.avro.generator.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetType
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.generator.logical.AvroKotlinLogicalTypeDefinition
import io.toolisticon.kotlin.avro.generator.logical.LogicalTypeMap
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.avro.value.Namespace
import org.apache.avro.Schema

@JvmInline
value class AvroPoetTypeMap(
  private val map: Map<AvroHashCode, AvroPoetType>
) : AvroPoetTypes {
  companion object {
    fun avroPoetTypeMap(
      rootClassName: ClassName? = null,
      properties: AvroKotlinGeneratorProperties,
      avroTypes: AvroTypesMap,
      logicalTypeMap: LogicalTypeMap,
    ): AvroPoetTypeMap {
      val result = mutableMapOf<AvroHashCode, AvroPoetType>()

      fun resolvePrimitive(avroType: AvroPrimitiveType): AvroPoetType {
        val logicalTypeDefinition: AvroKotlinLogicalTypeDefinition? = if (avroType is WithLogicalType) {
          avroType.logicalTypeName?.let { logicalTypeMap[it] }
        } else null

        val type = logicalTypeDefinition?.convertedType?.asTypeName()
          ?: when (avroType) {
            is BooleanType -> Boolean::class.asClassName()
            is BytesType -> ByteArray::class.asClassName()
            is DoubleType -> Double::class.asClassName()
            is FloatType -> Float::class.asClassName()
            is IntType -> Int::class.asClassName()
            is LongType -> Long::class.asClassName()
            NullType -> TODO("null should not happen")
            is StringType -> String::class.asClassName()
          }

        return AvroPoetType(avroType = avroType, typeName = type)
      }

      avroTypes.sequence().forEach { avroType ->
        when (avroType) {
          is NullType -> {}
          is AvroPrimitiveType -> {
            val t = resolvePrimitive(avroType)
            result[avroType.hashCode] = t
          }

          is UnionType -> {
            require(avroType.types.size == 2 && avroType.isNullable) { "Currently only optional types are supported for union." }

            // TODO: support this in UNION
            val type = requireNotNull(result[AvroHashCode.of(avroType.schema.get().extractNonNull())]) {
              "nonNull type for union does not exist: $result"
            }
            result[avroType.hashCode] = type.nullable(true)
          }

          is OptionalType -> {
            result[avroType.hashCode] = requireNotNull(result[avroType.type.hashCode]).nullable(true)
          }

          is ArrayType -> {
            val type = requireNotNull(result[avroType.elementType.hashCode]) {
              "element type for array does not exist: $result"
            }
            result[avroType.hashCode] = AvroPoetType(
              avroType = avroType, typeName = List::class.asClassName()
                .parameterizedBy(type.typeName)
            )
          }

          is MapType -> {
            val type = requireNotNull(result[avroType.valueType.hashCode]) {
              "element type for map does not exist: $result"
            }
            result[avroType.hashCode] = AvroPoetType(
              avroType = avroType, typeName = Map::class.asClassName()
                .parameterizedBy(
                  String::class.asTypeName(),
                  type.typeName
                )
            )
          }

          is AvroNamedType -> {
            val namespace = avroType.namespace ?: Namespace.EMPTY
            val suffixed = avroType.name.suffix(properties.schemaTypeSuffix)

            val typeName = if (rootClassName != null) {
              rootClassName.nestedClass(avroType.name.value)
            } else {
              ClassName(namespace.value, avroType.name.value)
            }

            val suffixedTypeName = if (rootClassName != null) {
              rootClassName.nestedClass(suffixed.value)
            } else {
              ClassName(namespace.value, suffixed.value)
            }

            result[avroType.hashCode] = AvroPoetType(
              avroType = avroType,
              typeName = typeName,
              suffixedTypeName = suffixedTypeName
            )
          }

          else -> {}

        }
      }


      return AvroPoetTypeMap(result)
    }
  }

  override fun get(hashCode: AvroHashCode): AvroPoetType = requireNotNull(map[hashCode]) { "type not found for hashCode=$hashCode" }
  override fun iterator(): Iterator<AvroPoetType> = map.values.iterator()

}

// TODO this was removed in avro4k 2, c&p here to compile again, need to figure out how to replace
@Deprecated("copy & paste from avro4k 1.10.1")
internal fun Schema.extractNonNull(): Schema = when (this.type) {
  Schema.Type.UNION -> this.types.filter { it.type != Schema.Type.NULL }.let { if (it.size > 1) Schema.createUnion(it) else it[0] }
  else -> this
}
