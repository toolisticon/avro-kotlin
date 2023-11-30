package io.toolisticon.avro.kotlin._bak

import io.toolisticon.avro.kotlin.AvroKotlin.Constants.NAME_SEPARATOR
import io.toolisticon.avro.kotlin.CanonicalName
import io.toolisticon.avro.kotlin.name.Name
import io.toolisticon.avro.kotlin.name.Namespace

/**
 * Default implementation of [AvroFqn].
 */
@Deprecated("remove")
data class AvroFqnData(override val namespace: Namespace, override val name: Name) : AvroFqn {

  override val canonicalName: CanonicalName = "$namespace${NAME_SEPARATOR}$name"
}
