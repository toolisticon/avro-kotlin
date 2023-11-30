package io.toolisticon.avro.kotlin

import java.io.File

/**
 * Combining namespace and name as a [java.lang.Class#canonicalName].
 */
typealias CanonicalName = String

/**
 * A file extension.
 */
typealias FileExtension = String

/**
 * The Schema fingerprint.
 */
typealias Fingerprint = Long

/**
 * Message encoded as [Single Object](https://avro.apache.org/docs/current/spec.html#single_object_encoding) ByteArray.
 */
typealias SingleObjectEncoded = ByteArray

/**
 * The encoded message. This is only the payload data,
 * so no marker header and encoded schemaId are present.
 */
typealias SingleObjectPayload = ByteArray

typealias Directory = File
