@file: JvmName("KotlinKtx")

package io.toolisticon.avro.kotlin.ktx

fun <T : Any> Result<List<T>?>.orEmpty(): List<T> = getOrNull() ?: emptyList()
