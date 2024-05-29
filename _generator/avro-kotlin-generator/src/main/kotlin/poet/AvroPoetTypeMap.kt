package io.toolisticon.kotlin.avro.generator.poet

import com.github.avrokotlin.avro4k.schema.extractNonNull
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.api.AvroPoetType
import io.toolisticon.kotlin.avro.generator.api.AvroPoetTypes
import io.toolisticon.kotlin.avro.generator.api.processor.AvroKotlinLogicalTypeDefinition
import io.toolisticon.kotlin.avro.generator.api.processor.LogicalTypeMap
import io.toolisticon.kotlin.avro.model.*
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.avro.value.Namespace

@JvmInline
value class AvroPoetTypeMap(
  private val map: Map<AvroHashCode, AvroPoetType>
) : AvroPoetTypes {
  companion object {
    fun avroPoetTypeMap(
      rootClassName: ClassName,
      properties: AvroKotlinGeneratorProperties,
      avroTypes: AvroTypesMap,
      logicalTypeMap: LogicalTypeMap,
    ): AvroPoetTypeMap {
      val result = mutableMapOf<AvroHashCode, AvroPoetType>()

      fun resolvePrimitive(avroType: AvroPrimitiveType): AvroPoetType {
        val logicalTypeDefinition: AvroKotlinLogicalTypeDefinition = if (avroType is WithLogicalType) {
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

            result[avroType.hashCode] = AvroPoetType(
              avroType = avroType,
              typeName = rootClassName.nestedClass(avroType.name.value),
              suffixedTypeName = rootClassName.nestedClass(suffixed.value)
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

