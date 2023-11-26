package io.toolisticon.avro.kotlin.model

import io.toolisticon.avro.kotlin.value.Graph
import io.toolisticon.avro.kotlin.value.AvroHashCode
import org.apache.avro.Protocol

/**
 * The types map contains a unique key/value pair of
 * all schemas contained in a declaration file.
 */
class AvroTypesMap private constructor(
  private val map: Map<AvroHashCode, AvroType>,
  val graph: Graph<AvroHashCode>,
) : Map<AvroHashCode, AvroType> by map {

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

    @Deprecated(message = "move to AvroProtocol", replaceWith = ReplaceWith("AvroProtocol"))
    constructor(protocol: Protocol) : this(
      buildList {
        addAll(protocol.types.map { AvroSchema(it) })

        protocol.messages.values.forEach { msg ->
          msg.request.fields.forEach { schemaField ->
            add(AvroSchema(schemaField.schema())) // TODO; use name
          }
          add(AvroSchema(msg.response))
          add(AvroSchema(msg.errors))
        }
      }
    )

    operator fun plus(schemas: List<AvroSchema>) = schemas.fold(this) { acc, cur -> acc + cur }

    operator fun plus(schema: AvroSchema): SchemaCatalog {
      val hashCode = schema.hashCode
      return if (map.containsKey(hashCode)) {
        this
      } else {
        val g = schema.enclosedTypes().map { hashCode to it.hashCode }.fold(graph + hashCode) { a, c -> a + c }
        copy(
          map = map + mapOf(hashCode to schema),
          graph = g
        ).plus(schema.enclosedTypes())
      }
    }

    fun typesMap(): Map<AvroHashCode, AvroType> = entries.fold(LinkedHashMap()) { acc, cur ->
      acc.apply {
        computeIfAbsent(cur.key) { _ -> cur.value.avroType() }
      }
    }
  }

  private constructor(catalog: SchemaCatalog) : this(map = catalog.typesMap(), catalog.graph)

  constructor(schema: AvroSchema) : this(catalog = SchemaCatalog(schema))

  constructor(protocol: AvroProtocol) : this(emptyMap(), Graph())

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

  /**
   * Contained [AvroType]s in order of directed dependency graph.
   */
  fun sequence(): Sequence<AvroType> = graph.sequence.mapNotNull { get(it) }
}
