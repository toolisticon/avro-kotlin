package io.toolisticon.lib.avro

import io.toolisticon.lib.avro.fqn.AvroFqn
import io.toolisticon.lib.avro.fqn.AvroFqnData
import io.toolisticon.lib.avro.fqn.ProtocolFqn
import io.toolisticon.lib.avro.fqn.SchemaFqn

object AvroKotlinLib {

  fun schema(namespace: Namespace, name: Name): SchemaFqn = SchemaFqn(namespace = namespace, name = name)
  fun schema(fqn: AvroFqn): SchemaFqn = schema(namespace = fqn.namespace, name = fqn.name)

  fun protocol(namespace: Namespace, name: Name): ProtocolFqn = ProtocolFqn(namespace = namespace, name = name)
  fun protocol(fqn: AvroFqn): ProtocolFqn = protocol(namespace = fqn.namespace, name = fqn.name)


  /**
   * Concat a canonical name based on namespace and name.
   */
  fun canonicalName(namespace: Namespace, name: Name): CanonicalName = "$namespace$NAME_SEPARATOR$name"

  /**
   * Create [AvroFqn] based on namespace and name.
   */
  fun fqn(namespace: Namespace, name: Name): AvroFqn = AvroFqnData(namespace = namespace, name = name)

  /**
   * Avro Schema files end with `.avsc`.
   */
  const val EXTENSION_SCHEMA: FileExtension = "avsc"

  /**
   * Avro protocol files end with `.avpr`.
   */
  const val EXTENSION_PROTOCOL: FileExtension = "avpr"

  /**
   * Default separator used in canonical name.
   */
  const val NAME_SEPARATOR = "."

  /**
   * Default charset to use is [Charsets#UTF_8]
   */
  val UTF_8 = Charsets.UTF_8

  /**
   * Default [ClassLoader] for resource loading.
   */
  val DEFAULT_CLASS_LOADER: ClassLoader = AvroKotlinLib::class.java.classLoader

}
