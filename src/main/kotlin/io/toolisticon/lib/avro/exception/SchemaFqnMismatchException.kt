package io.toolisticon.lib.avro.exception

import io.toolisticon.lib.avro.fqn.SchemaFqn
import org.apache.avro.AvroRuntimeException

class SchemaFqnMismatchException(namespace: String, name: String, path: String) : AvroRuntimeException("found schema fqn='$namespace.$name' did not match path='$path'") {
  constructor(expected:SchemaFqn, actual:SchemaFqn) : this(expected.namespace, expected.name, actual.path.toString())
}
