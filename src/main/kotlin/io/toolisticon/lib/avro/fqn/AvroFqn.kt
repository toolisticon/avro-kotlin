package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.AvroKotlinLib.canonicalName
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
   * Combining namespace and name.
   */
  val canonicalName: CanonicalName

}

/**
 * Default data-class implementing [AvroFqn].
 */
data class AvroFqnData(override val namespace: Namespace, override val name: Name) : AvroFqn {

  override val canonicalName: CanonicalName by lazy {
    canonicalName(namespace, name)
  }
}
