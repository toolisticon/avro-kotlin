package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin.CanonicalName
import io.toolisticon.avro.kotlin.name.Name
import io.toolisticon.avro.kotlin.name.Namespace


/**
 * Tuple containing namespace and name.
 */
@Deprecated("remove")
interface AvroFqn {

  /**
   * Schema/Protocol namespace.
   */
  val namespace: Namespace

  /**
   * Schema/Protocol name.
   */
  val name: Name

  /**
   * Concat a canonical name based on namespace and name.
   */
  val canonicalName: CanonicalName

}

