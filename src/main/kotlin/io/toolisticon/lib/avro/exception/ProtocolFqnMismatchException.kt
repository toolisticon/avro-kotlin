package io.toolisticon.lib.avro.exception

import org.apache.avro.AvroRuntimeException

class ProtocolFqnMismatchException(namespace: String, name: String, path: String) : AvroRuntimeException("protocol fqn='$namespace.$name' did not match path='$path'")
