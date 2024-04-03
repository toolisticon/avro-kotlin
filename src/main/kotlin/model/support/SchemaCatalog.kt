package io.toolisticon.avro.kotlin.model.support

import io.toolisticon.avro.kotlin.AvroKotlin.orEmpty
import io.toolisticon.avro.kotlin.model.SchemaType
import io.toolisticon.avro.kotlin.model.support.SchemaCatalog.Companion.bySchemaType
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Graph
import org.apache.avro.Schema

/**
 * Internal helper class to create the map of enclosed schemas. We can have self-referencing schemas and need to carefully remove them to avoid
 * endless loops or duplicates, and this proved to be working.
 */
internal data class SchemaCatalog(
  private val map: Map<AvroHashCode, Schema> = mapOf(),
  val graph: Graph<AvroHashCode> = Graph()
) : Map<AvroHashCode, Schema> by map {
  companion object {

    fun bySchemaType(schemaType: SchemaType): (Map.Entry<AvroHashCode, Schema>) -> Boolean = { schemaType.get() == it.value.type }


    /**
     * Gets all enclosed schema references that are used by this schema, these are
     *
     * * elementType - if it is an array
     * * valueType - if it is a map
     * * fields.schema() - if it is a record
     */
    internal fun Schema.enclosed() = buildList<Schema> {
      runCatching { this@enclosed.elementType }.getOrNull()?.also(this::add)
      runCatching { this@enclosed.valueType }.getOrNull()?.also(this::add)
      addAll(runCatching { this@enclosed.fields.map { it.schema() } }.orEmpty())
      addAll(runCatching { this@enclosed.types }.orEmpty())
    }
  }

  /**
   * Creates a copy of given catalog. Required to use plus operator in other constructors.
   */
  private constructor(catalog: SchemaCatalog) : this(map = catalog.map, graph = catalog.graph)

  /**
   * Creates new instance based on given schema.
   */
  constructor(schema: Schema) : this(SchemaCatalog() + schema)

  /**
   * Creates new instance based on given schemas.
   */
  constructor(schemas: List<Schema>) : this(SchemaCatalog() + schemas)

  /**
   * Adds all schemas to current catalog.
   *
   * Uses plus(Schema)
   */
  operator fun plus(schemas: List<Schema>) = schemas.fold(this) { a, c -> a + c }

  /**
   * Adds a single schema and all of its enclosed types to the current catalog.
   */
  operator fun plus(schema: Schema): SchemaCatalog {
    val hashCode = AvroHashCode(schema)

    return if (contains(hashCode)) {
      this
    } else {
      val enclosed = schema.enclosed()

      copy(
        map = map + mapOf(hashCode to schema),
        graph = graph + hashCode + enclosed.map { hashCode to AvroHashCode(it) }
      ).plus(enclosed)
    }
  }
}

/**
 * Convenience filter for single entry of schemaType.
 */
internal fun SchemaCatalog.single(schemaType: SchemaType): Pair<AvroHashCode, Schema> = filter(bySchemaType(schemaType)).entries.single().toPair()

//} else {
//val g = schema.enclosedTypes.map { hashCode to it.hashCode }.fold(graph + hashCode) { a, c -> a + c }
//copy(
//map = map + mapOf(hashCode to schema),
//graph = g
//).plus(schema.enclosedTypes)
//}
//}
//
//fun typesMap(): Map<AvroHashCode, AvroType> = entries.fold(LinkedHashMap()) { acc, cur ->
//acc.apply {
//computeIfAbsent(cur.key) { _ -> AvroType.avroType(cur.value) }

/*private fun recurseEnclosed(schema: Schema, hashCode: AvroHashCode, map: MutableMap<AvroHashCode, Schema> = mutableMapOf()): Map<AvroHashCode, Schema> {
  if (!map.containsKey(hashCode)) {
    map[hashCode] = schema

    val enclosed = buildList<Schema> {
      runCatching { schema.elementType }.getOrNull()?.also(this::add)
      runCatching { schema.valueType }.getOrNull()?.also(this::add)
      addAll(runCatching { schema.fields.map { it.schema() } }.orEmpty())
      addAll(kotlin.runCatching { schema.types }.orEmpty())
    }

    enclosed.forEach {
      it.recurseEnclosed(map)
    }
  }

  return map
}*/
