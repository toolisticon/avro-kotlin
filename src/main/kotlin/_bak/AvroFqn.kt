package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace


/**
 * Tuple containing namespace and name.
 */
@Deprecated("remove")
interface AvroFqn {

  /**
   * Schema/Protocol namespace.
   */
  val namespace: Namespace get() = canonicalName.namespace

  /**
   * Schema/Protocol name.
   */
  val name: Name get() = canonicalName.name

  /**
   * Concat a canonical name based on namespace and name.
   */
  val canonicalName: CanonicalName

}

