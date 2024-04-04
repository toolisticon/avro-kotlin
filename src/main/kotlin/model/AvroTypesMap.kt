package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.model.wrapper.AvroProtocol
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema
import io.toolisticon.avro.kotlin.model.wrapper.SchemaCatalog
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

    internal fun SchemaCatalog.typesMap(): Map<AvroHashCode, AvroType> = entries.fold(LinkedHashMap()) { acc, cur ->
      acc.apply {
        computeIfAbsent(cur.key) { _ -> AvroType.avroType(cur.value) }
      }
    }
  }

  init {
    require(map.keys.sorted() == graph.vertexes) { "not all hashes contained: ${map.keys}, ${graph.vertexes}" }
  }

  internal constructor(catalog: SchemaCatalog) : this(map = catalog.typesMap(), catalog.graph)

  constructor(schema: AvroSchema) : this(catalog = SchemaCatalog(schema))
  constructor(schemas: List<AvroSchema>) : this(catalog = SchemaCatalog(schemas))

  constructor(protocol: AvroProtocol) : this(
    catalog = protocol.messages.values.fold(
      SchemaCatalog(protocol.get().types.map { AvroSchema(it) })
    ) { catalog, message ->
      catalog + message.enclosedTypes()
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

  val allHashCodes: Set<AvroHashCode> by lazy {
    LinkedHashSet(graph.vertexes)
  }

  fun sub(hashCode: AvroHashCode): AvroTypesMap {
    val newGraph = graph.subGraphFor(hashCode)
    val remainingHashCodes = newGraph.vertexes.toSet()
    val newMap = map.filter { remainingHashCodes.contains(it.key) }

    return AvroTypesMap(graph = newGraph, map = newMap)
  }

  val schemas: List<AvroSchema> by lazy {
    values.map { it.schema }
  }
}
