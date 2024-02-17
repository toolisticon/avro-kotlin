package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.model.SchemaType.Group.*
import io.toolisticon.avro.kotlin.model.wrapper.TypeSupplier
import org.apache.avro.Schema.Type
import java.util.*

/**
 * Wraps a [Schema#Type] for type safe access and support functions.
 */
enum class SchemaType(
  val group: Group,
  private val type: Type
) : TypeSupplier {

  ENUM(group = NAMED, type = Type.ENUM),
  FIXED(group = NAMED, type = Type.FIXED),
  RECORD(group = NAMED, type = Type.RECORD),

  ARRAY(group = CONTAINER, type = Type.ARRAY),
  MAP(group = CONTAINER, type = Type.MAP),
  UNION(group = CONTAINER, type = Type.UNION),

  BOOLEAN(group = PRIMITIVE, type = Type.BOOLEAN),
  BYTES(group = PRIMITIVE, type = Type.BYTES),
  DOUBLE(group = PRIMITIVE, type = Type.DOUBLE),
  FLOAT(group = PRIMITIVE, type = Type.FLOAT),
  INT(group = PRIMITIVE, type = Type.INT),
  LONG(group = PRIMITIVE, type = Type.LONG),
  NULL(group = PRIMITIVE, type = Type.NULL),
  STRING(group = PRIMITIVE, type = Type.STRING),
  ;

  enum class Group {
    NAMED,
    CONTAINER,
    PRIMITIVE,
    ;

    val types: EnumSet<SchemaType> by lazy {
      SchemaType.valuesOfGroup(this)
    }
  }

  companion object {
    private val byGroup: Map<Group, EnumSet<SchemaType>> = entries.groupBy { it.group }.entries.associate { it.key to EnumSet.copyOf(it.value) }
    private val byType: Map<Type, SchemaType> = entries.associateBy { it.type }

    // cannot be null
    fun valueOfType(type: Type) = byType[type]!!

    // can never be null
    fun valuesOfGroup(group: Group): EnumSet<SchemaType> = byGroup[group]!!

    val PRIMITIVE_TYPES: EnumSet<SchemaType> = valuesOfGroup(PRIMITIVE)
  }

  val isPrimitive = PRIMITIVE == group

  val isNamed = NAMED == group

  val isContainer = CONTAINER == group

  override fun get(): Type = type

  val displayName: String = type.getName()
}
