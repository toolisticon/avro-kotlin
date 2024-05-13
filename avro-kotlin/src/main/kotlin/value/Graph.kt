package io.toolisticon.avro.kotlin.value

/**
 * A Graph implementation that represents the dependencies (ars) between its vertexes.
 *
 * This is used to determinate the order of dependencies, returning the dependant elements last.
 */
@JvmInline
value class Graph<T : Comparable<T>> private constructor(
  private val map: Map<T, Set<T>>
) : Iterable<T> {

  private operator fun Map<T, Set<T>>.plus(entry: Pair<T, Set<T>>): Map<T, Set<T>> = this.toMutableMap().apply {
    compute(entry.first) { _, existing ->
      (existing ?: emptySet()) + entry.second
    }
  }.toMap()

  private operator fun Map<T, Set<T>>.minus(vertex: T): Map<T, Set<T>> = this.toMutableMap().apply {
    remove(vertex)
    entries.forEach {
      computeIfPresent(it.key) { _, existing -> existing - vertex }
    }
  }.toMap()

  constructor() : this(mapOf())
  constructor(vertex: T) : this((Graph<T>() + vertex).map)
  constructor(vararg arcs: Pair<T, T>) : this(arcs.toList())
  constructor(arcs: List<Pair<T, T>>) : this(arcs.fold(Graph<T>()) { acc, cur -> acc + cur }.map)


  operator fun plus(arc: Pair<T, T>): Graph<T> = if (arc.first == arc.second) {
    plus(arc.first)
  } else {
    Graph(map + (arc.first to setOf(arc.second)) + (arc.second to emptySet()))
  }

  operator fun plus(arcs: List<Pair<T, T>>) = arcs.fold(this) { acc, cur -> acc + cur }

  operator fun plus(graph: Graph<T>): Graph<T> = graph.arcs.fold(this) { acc, cur -> acc + cur }

  operator fun plus(vertex: T): Graph<T> = Graph(map + (vertex to emptySet()))

  operator fun minus(vertex: T): Graph<T> = Graph(map - vertex)

  internal fun copy() = Graph(map)

  val vertexes: List<T> get() = map.values.fold(map.keys) { acc, cur -> acc + cur }.sorted()

  val arcs: List<Pair<T, T>>
    get() = map.entries
      .sortedBy { it.key }
      .fold(listOf()) { acc, cur ->
        acc + cur.value.map { cur.key to it }
      }

  fun contains(arc: Pair<T, T>): Boolean = map[arc.first]?.contains(arc.second) ?: false
  fun contains(vertex: T): Boolean = map.containsKey(vertex)

  val sequence: Sequence<T>
    get() = sequence {
      var graph = copy()

      while (graph.map.isNotEmpty()) {
        val vertex = graph.map.filter { it.value.isEmpty() }.map { it.key }.minOf { it }

        graph -= vertex
        yield(vertex)
      }
    }

  fun subGraphFor(vararg vertexes: T): Graph<T> {
    require(vertexes.all { contains(it) }) { "Unknown vertexes: ${vertexes.toList() - this.vertexes.toSet()}" }
    val keep = vertexes.flatMap { map[it]!!+it }.toSet()
    val remove = this.vertexes - keep

    return remove.fold(this.copy()) { graph, removeVertex ->
      graph - removeVertex
    }
  }


  fun isEmpty() = map.isEmpty()

  override fun iterator(): Iterator<T> = sequence.iterator()

  override fun toString() = map.toString()
}
