package io.toolisticon.kotlin.avro.serialization

import io.toolisticon.avro.kotlin.model.SchemaType
import kotlinx.serialization.descriptors.PrimitiveKind

val SchemaType.BOOLEAN.kind: PrimitiveKind get() = PrimitiveKind.BOOLEAN
val SchemaType.BYTES.kind: PrimitiveKind get() = PrimitiveKind.BYTE // FIXME: should we support it?
val SchemaType.DOUBLE.kind: PrimitiveKind get() = PrimitiveKind.DOUBLE
val SchemaType.FLOAT.kind: PrimitiveKind get() = PrimitiveKind.FLOAT
val SchemaType.INT.kind: PrimitiveKind get() = PrimitiveKind.INT
val SchemaType.LONG.kind: PrimitiveKind get() = PrimitiveKind.LONG
val SchemaType.STRING.kind: PrimitiveKind get() = PrimitiveKind.STRING
