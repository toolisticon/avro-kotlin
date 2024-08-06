package io.toolisticon.kotlin.avro.generator.processor


import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asTypeName
import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorApi.avroClassName
import io.toolisticon.kotlin.avro.generator.api.AvroKotlinGeneratorProperties
import io.toolisticon.kotlin.avro.generator.context.AvroKotlinGeneratorContextFactory
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.model.wrapper.AvroSchema
import io.toolisticon.kotlin.generation.builder.KotlinDataClassSpecBuilder
import kotlinx.serialization.SerialName
import org.apache.avro.SchemaBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class AddAvroAnnotationsToTypeSpecProcessorTest {
  private val processor = AddAvroAnnotationsToTypeSpecProcessor()

  @Test
  fun `add annotation to topLevel spec`() {
    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .requiredBoolean("b")
        .endRecord()
    )
    val recordType = declaration.recordType

    val properties = AvroKotlinGeneratorProperties(schemaTypeSuffix = "Dummy")
    val className = avroClassName(recordType, properties)
    val ctx = AvroKotlinGeneratorContextFactory(properties).create(declaration)

    val builder = KotlinDataClassSpecBuilder.builder(className)
      .addConstructorProperty("x", String::class)
    processor.addAvroNamedAnnotation(recordType, className, builder)

    val typeSpec = builder.build()

    val avroName = typeSpec.get().annotations.firstOrNull { it.typeName == SerialName::class.asTypeName() }

    assertThat(avroName).isNotNull
    assertThat(avroName!!.members.first().toString()).isEqualTo("""value = "Bar"""")
  }


  @Test
  fun `add annotation to nested spec`() {
    val subTypeRecord = RecordType(
      AvroSchema(
        SchemaBuilder.record("foo.Foo")
          .fields()
          .requiredBoolean("b")
          .endRecord()
      )
    )

    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("foo.Bar")
        .fields()
        .name("f")
        .type(subTypeRecord.get())
        .noDefault()
        .endRecord()
    )
    val recordType = declaration.recordType

    val properties = AvroKotlinGeneratorProperties(schemaTypeSuffix = "XXX")
    val className = avroClassName(subTypeRecord, properties)
    val ctx = AvroKotlinGeneratorContextFactory(properties).create(declaration)

    val builder = KotlinDataClassSpecBuilder.builder(className)
      .addConstructorProperty("x", String::class)
    processor.addAvroNamedAnnotation(subTypeRecord, className, builder)

    val typeSpec = builder.build()

    val avroName = typeSpec.get().annotations.firstOrNull { it.typeName == SerialName::class.asTypeName() }

    assertThat(avroName!!.members.first()).isEqualTo(CodeBlock.of("value = %S", "Foo"))
  }
}
