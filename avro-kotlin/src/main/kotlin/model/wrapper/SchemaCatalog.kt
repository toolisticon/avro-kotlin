package io.toolisticon.kotlin.avro.model.wrapper

import io.toolisticon.kotlin.avro.AvroKotlin.orEmpty
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchemaChecks.isPrimitive
import io.toolisticon.kotlin.avro.model.wrapper.SchemaCatalog.Companion.bySchemaType
import io.toolisticon.kotlin.avro.value.AvroHashCode
import io.toolisticon.kotlin.avro.value.Graph
import org.apache.avro.Schema

/**
 * Internal helper class to create the map of enclosed schemas. We can have self-referencing schemas and need to carefully remove them to avoid
 * endless loops or duplicates, and this proved to be working.
 */
internal class SchemaCatalog private constructor(
  private val map: Map<AvroHashCode, AvroSchema>,
  val graph: Graph<AvroHashCode>
) : Map<AvroHashCode, AvroSchema> by map {
  companion object {
    private val EMPTY = SchemaCatalog(map = emptyMap(), graph = Graph())

    fun notPrimitive(): (Map.Entry<AvroHashCode, AvroSchema>) -> Boolean = { !(it.value.isPrimitive) }
    fun bySchemaType(schemaType: SchemaType): (Map.Entry<AvroHashCode, AvroSchema>) -> Boolean = { schemaType == it.value.type }


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
    }.map { AvroSchema(it) }
  }

  /**
   * Creates a copy of given catalog. Required to use plus operator in other constructors.
   */
  private constructor(catalog: SchemaCatalog) : this(map = catalog.map, graph = catalog.graph)

  /**
   * Creates new instance based on given schema.
   */
  constructor(schema: AvroSchema, excludeSelf: Boolean = false) : this(catalog = (EMPTY + schema).let {
    if (excludeSelf) it - schema.hashCode
    else it
  })

  /**
   * Creates new instance based on given schemas.
   */
  constructor(schemas: List<AvroSchema>) : this(catalog = EMPTY + schemas)

  /**
   * Adds all schemas to current catalog.
   *
   * Uses plus(Schema)
   */
  operator fun plus(schemas: List<AvroSchema>) = schemas.fold(this) { a, c -> a + c }

  /**
   * Adds a single schema and all of its enclosed types to the current catalog.
   */
  operator fun plus(schema: AvroSchema): SchemaCatalog {
    return if (contains(schema.hashCode)) {
      this
    } else {
      val enclosed = schema.get().enclosed()

      SchemaCatalog(
        map = map + mapOf(schema.hashCode to schema),
        graph = graph + schema.hashCode + enclosed.map { schema.hashCode to it.hashCode }
      ).plus(enclosed)
    }
  }

  operator fun minus(hashCode: AvroHashCode) = SchemaCatalog(
    map = map - hashCode,
    graph = graph - hashCode
  )

  val allHashCodes: Set<AvroHashCode> by lazy {
    LinkedHashSet(graph.vertexes)
  }

  fun sub(hashCode: AvroHashCode): SchemaCatalog {
    val newGraph = graph.subGraphFor(hashCode)
    val remainingHashCodes = newGraph.vertexes.toSet()
    val newMap = map.filter { remainingHashCodes.contains(it.key) }

    return SchemaCatalog(graph = newGraph, map = newMap)
  }
}

internal fun SchemaCatalog.single(predicate: (Map.Entry<AvroHashCode, AvroSchema>) -> Boolean) = filter(predicate).entries.single().toPair()

/**
 * Convenience filter for single entry of schemaType.
 */
internal fun SchemaCatalog.single(schemaType: SchemaType): Pair<AvroHashCode, AvroSchema> = single(bySchemaType(schemaType))
