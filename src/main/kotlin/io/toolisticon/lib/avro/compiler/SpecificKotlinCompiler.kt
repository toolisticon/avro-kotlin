package io.toolisticon.lib.avro.compiler

import org.apache.avro.JsonProperties
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.apache.avro.compiler.specific.SpecificCompiler
import org.apache.avro.generic.GenericData
import org.apache.avro.specific.SpecificData
import java.io.File

class SpecificKotlinCompiler(
  val compiler: SpecificCompiler
)  {
  companion object {
    private val NULL_SCHEMA = Schema.create(Schema.Type.NULL)
  }

  private val fieldSpecificData = SpecificCompiler::class.java.getDeclaredField("specificData").apply {
    this.isAccessible = true
  }

  private val methodGetConvertedLogicalType = SpecificCompiler::class.java
    .getDeclaredMethod("getConvertedLogicalType", Schema::class.java).apply {
      isAccessible = true
    }

  private val methodMangleFullyQualified = SpecificCompiler::class.java
    .getDeclaredMethod("mangleFullyQualified", String::class.java).apply {
      isAccessible = true
    }

  private val methodGetStringType = SpecificCompiler::class.java
    .getDeclaredMethod("getStringType", Any::class.java).apply {
      isAccessible = true
    }

  val specificData: SpecificData = fieldSpecificData.get(compiler) as SpecificData

  fun mangleFullyQualified(fullName: String): String = methodMangleFullyQualified.invoke(compiler, fullName) as String

  fun getStringType(overrideClassProperty: Any?): String = methodGetStringType.invoke(
    compiler,
    overrideClassProperty
  ) as String


  /**
   * Utility for template use. Returns the unboxed java type for a Schema
   * including the void type.
   */
  fun javaUnbox(schema: Schema, unboxNullToVoid: Boolean): String {
    val convertedLogicalType: String? = methodGetConvertedLogicalType.invoke(compiler, schema) as String?
    return convertedLogicalType
      ?: when (schema.type) {
        Schema.Type.INT -> "Int"
        Schema.Type.LONG -> "Long"
        Schema.Type.FLOAT -> "Float"
        Schema.Type.DOUBLE -> "Double"
        Schema.Type.BOOLEAN -> "Boolean"
        Schema.Type.NULL -> {
          if (unboxNullToVoid) {
            // Used for preventing unnecessary returns for RPC methods without response but
            // with error(s)
            "void"
          } else javaType(schema, false)
        }

        else -> javaType(schema, false)
      }
  }

  private fun javaType(schema: Schema, checkConvertedLogicalType: Boolean): String {
    if (checkConvertedLogicalType) {
      val convertedLogicalType: String? = methodGetConvertedLogicalType.invoke(compiler, schema) as String?
      if (convertedLogicalType != null) {
        return convertedLogicalType
      }
    }
    return when (schema.type) {
      Schema.Type.RECORD, Schema.Type.ENUM, Schema.Type.FIXED -> mangleFullyQualified(schema.fullName)
      Schema.Type.ARRAY -> "java.util.List<" + javaType(schema.elementType, false) + ">"
      Schema.Type.MAP -> ("java.util.Map<" + getStringType(schema.getObjectProp(SpecificData.KEY_CLASS_PROP)) + ","
        + javaType(schema.valueType, false) + ">")

      Schema.Type.UNION -> {
        val types = schema.types // elide unions with null
        if (types.size == 2 && types.contains(NULL_SCHEMA)) javaType(types[if (types[0] == NULL_SCHEMA) 1 else 0], false) else "java.lang.Object"
      }

      Schema.Type.STRING -> getStringType(schema.getObjectProp(SpecificData.CLASS_PROP))
      Schema.Type.BYTES -> "java.nio.ByteBuffer"
      Schema.Type.INT -> "java.lang.Integer"
      Schema.Type.LONG -> "java.lang.Long"
      Schema.Type.FLOAT -> "java.lang.Float"
      Schema.Type.DOUBLE -> "java.lang.Double"
      Schema.Type.BOOLEAN -> "java.lang.Boolean"
      Schema.Type.NULL -> "java.lang.Void"
      else -> throw RuntimeException("Unknown type: $schema")
    }
  }

  fun getStringType(s: Schema?): String  = TODO()

  //override fun javaUnbox(schema: Schema?): String = TODO()

  fun javaType(schema: Schema?): String = TODO()

  fun addLogicalTypeConversions(specificData: SpecificData?) :Unit= TODO()

  fun isCreateAllArgsConstructor(): Boolean = TODO()

  fun setAdditionalVelocityTools(additionalVelocityTools: MutableList<Any>?):Unit = TODO()

  fun setTemplateDir(templateDir: String?) :Unit= TODO()

  fun setSuffix(suffix: String?):Unit = TODO()

  fun publicFields(): Boolean = TODO()

  fun privateFields(): Boolean = TODO()

  fun setFieldVisibility(fieldVisibility: SpecificCompiler.FieldVisibility?) :Unit= TODO()

  fun isCreateSetters(): Boolean = TODO()

  fun setCreateSetters(createSetters: Boolean) :Unit= TODO()

  fun isCreateOptionalGetters(): Boolean = TODO()

  fun setCreateOptionalGetters(createOptionalGetters: Boolean) :Unit= TODO()
  fun isGettersReturnOptional(): Boolean = TODO()

  fun setGettersReturnOptional(gettersReturnOptional: Boolean) :Unit= TODO()

  fun isOptionalGettersForNullableFieldsOnly(): Boolean = TODO()

  fun setOptionalGettersForNullableFieldsOnly(optionalGettersForNullableFieldsOnly: Boolean) :Unit= TODO()

  fun setEnableDecimalLogicalType(enableDecimalLogicalType: Boolean):Unit = TODO()

  fun addCustomConversion(conversionClass: Class<*>?) :Unit= TODO()

  fun getUsedConversionClasses(schema: Schema?): MutableCollection<String> = TODO()

  fun getUsedCustomLogicalTypeFactories(schema: Schema?): MutableMap<String, String> = TODO()


  fun compileToDestination(src: File?, dst: File?):Unit = TODO()

  fun calcAllArgConstructorParameterUnits(record: Schema?): Int = TODO()

  fun validateRecordForCompilation(record: Schema?) :Unit= TODO()

  fun setStringType(t: GenericData.StringType?) :Unit= TODO()

  fun isStringable(schema: Schema?): Boolean = TODO()

  fun generateSetterCode(schema: Schema?, name: String?, pname: String?): String = TODO()

  fun indent(n: Int): String = TODO()

  fun getNonNullIndex(s: Schema?): Int = TODO()

  fun isCustomCodable(schema: Schema?): Boolean = TODO()

  fun hasLogicalTypeField(schema: Schema?): Boolean = TODO()

  fun conversionInstance(schema: Schema?): String = TODO()

  fun javaAnnotations(props: JsonProperties?): Array<String> = TODO()

  fun javaSplit(s: String?): String = TODO()

  fun setOutputCharacterEncoding(outputCharacterEncoding: String?): Unit = TODO()
}
