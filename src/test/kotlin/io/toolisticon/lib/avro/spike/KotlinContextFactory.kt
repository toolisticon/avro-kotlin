package io.toolisticon.lib.avro.spike

import io.toolisticon.lib.avro.ext.SchemaExt.fingerprint
import org.apache.avro.Schema
import org.apache.avro.compiler.specific.SpecificCompiler

data class KotlinContext(
  val compiler: SpecificCompiler,
  val schema: Schema,
) {
  val namespace: String = SpecificCompiler.mangle(schema.namespace)
  val fingerprint = schema.fingerprint
  val typeName = compiler.javaSplit(schema.toString())
  val schemaString = schema.toString()

//  fun kotlinType(): String =  when (schema.type) {
//      Schema.Type.RECORD, Schema.Type.ENUM, Schema.Type.FIXED -> mangleFullyQualified(schema.fullName)
//      Schema.Type.ARRAY -> "java.util.List<" + javaType(schema.elementType) + ">"
//      Schema.Type.MAP -> ("java.util.Map<" + getStringType(schema.getObjectProp(SpecificData.KEY_CLASS_PROP)) + ","
//        + javaType(schema.valueType) + ">")
//
//      Schema.Type.UNION -> {
//        val types = schema.types // elide unions with null
//        if (types.size == 2 && types.contains(SpecificCompiler.NULL_SCHEMA)) javaType(types[if (types[0] == SpecificCompiler.NULL_SCHEMA) 1 else 0]) else "java.lang.Object"
//      }
//
//      Schema.Type.STRING -> getStringType(schema.getObjectProp(SpecificData.CLASS_PROP))
//      Schema.Type.BYTES -> "java.nio.ByteBuffer"
//      Schema.Type.INT -> "java.lang.Integer"
//      Schema.Type.LONG -> "java.lang.Long"
//      Schema.Type.FLOAT -> "java.lang.Float"
//      Schema.Type.DOUBLE -> "java.lang.Double"
//      Schema.Type.BOOLEAN -> "java.lang.Boolean"
//      Schema.Type.NULL -> "java.lang.Void"
//      else -> throw RuntimeException("Unknown type: $schema")
//    }
//  }
}

class KotlinContextFactory {
  fun create(compiler: SpecificCompiler, schema: Schema): KotlinContext = KotlinContext(compiler, schema)
}

