package io.toolisticon.kotlin.avro._ktx

internal object KotlinKtx {

  /**
   * Splits a list in a pair of first element and remaining list.
   */
  fun <T> List<T>.head(): Pair<T, List<T>> = toMutableList().let {
    val head = it.removeFirst()
    head to it.toList()
  }


  fun <T : Any> create(fn: () -> T): T = fn()

}
