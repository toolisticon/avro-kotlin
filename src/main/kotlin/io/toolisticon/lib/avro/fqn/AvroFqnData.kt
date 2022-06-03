package io.toolisticon.lib.avro.fqn

import io.toolisticon.lib.avro.CanonicalName
import io.toolisticon.lib.avro.Name
import io.toolisticon.lib.avro.Namespace
import io.toolisticon.lib.avro.ext.IoExt

/**
 * Default implementation of [AvroFqn].
 */
data class AvroFqnData(override val namespace: Namespace, override val name: Name) : AvroFqn {

  override val canonicalName: CanonicalName = "$namespace${IoExt.NAME_SEPARATOR}$name"
}
