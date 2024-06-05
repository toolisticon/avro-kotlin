package io.toolisticon.kotlin.avro.generator.logical

import com.github.avrokotlin.avro4k.ScalePrecision
import com.github.avrokotlin.avro4k.serializer.BigDecimalSerializer
import io.toolisticon.kotlin.avro.generator.api.AvroDeclarationContext
import io.toolisticon.kotlin.avro.generator.api.processor.AvroKotlinLogicalTypeDefinition
import io.toolisticon.kotlin.avro.logical.BuiltInLogicalType
import io.toolisticon.kotlin.avro.model.RecordField
import io.toolisticon.kotlin.avro.model.SchemaType
import io.toolisticon.kotlin.avro.model.WithLogicalType
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.builder.KotlinConstructorPropertyBuilder
import io.toolisticon.kotlin.generation.builder.KotlinParameterBuilder
import org.apache.avro.LogicalTypes.Decimal
import java.math.BigDecimal
import java.util.*


/**
 * The decimal logical type represents an arbitrary-precision signed decimal number of the form unscaled × 10-scale.
 *
 * A decimal logical type annotates Avro bytes or fixed types. The byte array must contain the two’s-complement representation of the unscaled integer value in big-endian byte order. The scale is fixed, and is specified using an attribute.
 *
 * The following attributes are supported:
 *
 * * scale, a JSON integer representing the scale (optional). If not specified the scale is 0.
 * * precision, a JSON integer representing the (maximum) precision of decimals stored in this type (required). For example, the following schema represents decimal numbers with a maximum precision of 4 and a scale of 2:
 *
 * ```json
 * {
 *   "type": "bytes",
 *   "logicalType": "decimal",
 *   "precision": 4,
 *   "scale": 2
 * }
 * ```
 *
 * Precision must be a positive integer greater than zero. If the underlying type is a fixed, then the precision is limited by its size. An array of length n can store at most floor(log10(28 × n - 1 - 1)) base-10 digits of precision.
 *
 * Scale must be zero or a positive integer less than or equal to the precision.
 *
 * For the purposes of schema resolution, two schemas that are decimal logical types match if their scales and precisions match.
 *
 * @see [LogicalType#Decimal](https://avro.apache.org/docs/current/specification/#decimal)
 */
// FIXME @AutoService(Avro4kLogicalTypeDefinition::class)
class DecimalDefinition() : AvroKotlinLogicalTypeDefinition(
  name = BuiltInLogicalType.DECIMAL.logicalTypeName,
  convertedType = BigDecimal::class,
  serializerType = BigDecimalSerializer::class,
  allowedTypes = setOf(SchemaType.BYTES, SchemaType.STRING)
) {

  override fun processDataClassParameterSpec(ctx: AvroDeclarationContext, field: RecordField, builder: KotlinConstructorPropertyBuilder) {
    super.processDataClassParameterSpec(ctx, field, builder)
    val type = ctx.avroType(field.schema.hashCode)

    val decimal = ((type as WithLogicalType).logicalType) as Decimal? ?: throw IllegalArgumentException("no decimal type present")

    builder.addAnnotation(
      KotlinCodeGeneration.annotationBuilder(ScalePrecision::class)
        .addMember(
          "scale=%L, precision=%L",
          decimal.scale,
          decimal.precision
        ).build()
    )
  }
}
