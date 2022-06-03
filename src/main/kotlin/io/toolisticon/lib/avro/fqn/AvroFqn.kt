package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.CanonicalName
import io.toolisticon.lib.avro.Name
import io.toolisticon.lib.avro.Namespace


/**
 * Tuple containing namespace and name.
 */
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

