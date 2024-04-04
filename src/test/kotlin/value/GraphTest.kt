package io.toolisticon.avro.kotlin.value

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GraphTest {

  @Test
  fun `operate graph`() {
    var g = Graph(1 to 2, 2 to 3, 2 to 4)
    assertThat(g.arcs)
      .containsExactly(
        1 to 2,
        2 to 3,
        2 to 4
      )

    g += 1 to 4
    assertThat(g.arcs)
      .containsExactly(
        1 to 2,
        1 to 4,
        2 to 3,
        2 to 4
      )

    g -= 3

    assertThat(g.arcs)
      .containsExactly(
        1 to 2,
        1 to 4,
        2 to 4
      )

    g -= 1

    assertThat(g.arcs)
      .containsExactly(
        2 to 4
      )
  }

  @Test
  fun `self reference`() {
    assertThat(Graph(1 to 1).arcs).isEmpty()
    assertThat(Graph(1 to 1).sequence.toList()).containsExactly(1)
  }

  @Test
  fun `verify sequence`() {
    val g = Graph(
      1 to 2,
      1 to 3,
      2 to 4
    ) + 5 + (4 to 6) + (3 to 5)

    assertThat(g.sequence.toList()).containsExactly(
      5, 3, 6, 4, 2, 1
    )
  }


  @Test
  fun `new graph from vararg`() {
    assertThat(Graph(1 to 2, 2 to 3, 2 to 4).arcs)
      .containsExactly(
        1 to 2,
        2 to 3,
        2 to 4
      )
  }

  @Test
  fun `new graph from list`() {
    val list = listOf(1 to 2, 2 to 3, 2 to 4)
    assertThat(Graph(list).arcs)
      .containsExactly(
        1 to 2,
        2 to 3,
        2 to 4
      )
  }

  @Test
  fun `new graph plus list`() {
    var g = Graph(1 to 2)
    val list = listOf(1 to 2, 2 to 3, 2 to 4)
    g += list
    assertThat((g + list).arcs)
      .containsExactly(
        1 to 2,
        2 to 3,
        2 to 4
      )
  }

  @Test
  fun `graph plus graph`() {
    val g = Graph(1 to 2) + Graph(2 to 3, 2 to 4)
    assertThat(g.arcs)
      .containsExactly(
        1 to 2,
        2 to 3,
        2 to 4,
      )
  }

  @Test
  fun `subgraph for 2`() {
    val g = Graph(1 to 2) + Graph(2 to 3, 2 to 4)

    val sub = g.subGraphFor(2)
    assertThat(sub.arcs)
      .containsExactly(
        2 to 3,
        2 to 4,
      )
  }

  @Test
  fun `subgraph for 2 and 3`() {
    val g = Graph(
      1 to 2,
      1 to 3,
      2 to 4,
      3 to 4,
      4 to 2,
    )

    val sub = g.subGraphFor(2, 3)
    assertThat(sub.arcs)
      .containsExactly(
        2 to 4,
        3 to 4,
        4 to 2
      )
  }
}
