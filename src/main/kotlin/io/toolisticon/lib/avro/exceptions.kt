package io.toolisticon.lib.avro

import org.apache.avro.AvroRuntimeException

class AvroSchemaFqnMismatch(namespace: String, name: String, path: String) : AvroRuntimeException("schema fqn='$namespace.$name' did not match path='$path'")
