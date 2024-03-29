package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.model.wrapper.AvroProtocol
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.value.AvroHashCode
import io.toolisticon.avro.kotlin.value.Graph
import io.toolisticon.avro.kotlin.value.Name
import kotlin.reflect.KClass

/**
 * The types map contains a unique key/value pair of
 * all schemas contained in a declaration file.
 */
class AvroTypesMap private constructor(
  private val map: Map<AvroHashCode, AvroType>,
  val graph: Graph<AvroHashCode>,
) : Map<AvroHashCode, AvroType> by map {

  companion object {
    val EMPTY = AvroTypesMap(map = emptyMap(), graph = Graph())
  }

  init {
    require(map.keys.sorted() == graph.vertexes) { "not all hashes contained: ${map.keys}, ${graph.vertexes}" }
  }

  /**
   * Internal helper class to create the map of enclosed schemas. We can have self-referencing schemas and need to carefully remove them to avoid
   * endless loops or duplicates, and this proved to be working.
   */
  internal data class SchemaCatalog(
    private val map: Map<AvroHashCode, AvroSchema> = mapOf(),
    internal val graph: Graph<AvroHashCode> = Graph()
  ) : Map<AvroHashCode, AvroSchema> by map {

    private constructor(catalog: SchemaCatalog) : this(map = catalog.map, graph = catalog.graph)

    constructor(schema: AvroSchema) : this(SchemaCatalog() + schema)

    constructor(schemas: List<AvroSchema>) : this(SchemaCatalog() + schemas)

    operator fun plus(schemas: List<AvroSchema>) = schemas.fold(this) { acc, cur -> acc + cur }

    operator fun plus(schema: AvroSchema): SchemaCatalog {
      val hashCode = schema.hashCode
      return if (map.containsKey(hashCode)) {
        this
      } else {
        val g = schema.enclosedTypes.map { hashCode to it.hashCode }.fold(graph + hashCode) { a, c -> a + c }
        copy(
          map = map + mapOf(hashCode to schema),
          graph = g
        ).plus(schema.enclosedTypes)
      }
    }

    fun typesMap(): Map<AvroHashCode, AvroType> = entries.fold(LinkedHashMap()) { acc, cur ->
      acc.apply {
        computeIfAbsent(cur.key) { _ -> AvroType.avroType(cur.value) }
      }
    }
  }

  private constructor(catalog: SchemaCatalog) : this(map = catalog.typesMap(), catalog.graph)

  constructor(schema: AvroSchema) : this(catalog = SchemaCatalog(schema))
  constructor(schemas: List<AvroSchema>) : this(catalog = SchemaCatalog(schemas))

  constructor(protocol: AvroProtocol) : this(
    catalog = protocol.messages.values.fold(
      SchemaCatalog(protocol.get().types.map { AvroSchema(it) })
    ) { acc, cur ->
      acc + cur.enclosedTypes()
    }
  )

  operator fun minus(hashCode: AvroHashCode): AvroTypesMap = AvroTypesMap(
    map = map.toMutableMap().apply { remove(hashCode) }.toMap(),
    graph = graph - hashCode
  )

  override fun toString() = "AvroTypesMap(map=$map)"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AvroTypesMap) return false

    if (map != other.map) return false

    return true
  }

  override fun hashCode(): Int {
    return map.hashCode()
  }

  operator fun get(name: Name): AvroType? = map.values.find { it.name == name }

  inline fun <reified T : AvroType> findTypes(filter: (T) -> Boolean = { true }): List<T> = values.filterIsInstance<T>()
    .filter(filter)
    .toList()

  val byType: Map<KClass<out AvroType>, List<AvroType>> by lazy {
    map.values.groupBy(keySelector = { it::class })
  }

  /**
   * Contained [AvroType]s in order of directed dependency graph.
   */
  fun sequence(): Sequence<AvroType> = graph.sequence.mapNotNull { get(it) }
}
