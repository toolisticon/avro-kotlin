package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin.value.CanonicalName
import io.toolisticon.avro.kotlin.value.Name
import io.toolisticon.avro.kotlin.value.Namespace

/**
 * Default implementation of [AvroFqn].
 */
@Deprecated("remove")
data class AvroFqnData(override val namespace: Namespace, override val name: Name) : AvroFqn {

  override val canonicalName: CanonicalName = CanonicalName(namespace to  name)
}
