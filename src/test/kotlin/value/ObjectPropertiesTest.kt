package io.toolisticon.avro.kotlin.value

import io.toolisticon.avro.kotlin.AvroKotlin
import io.toolisticon.avro.kotlin.builder.AvroBuilder.primitiveSchema
import io.toolisticon.avro.kotlin.model.RecordType
import io.toolisticon.avro.kotlin.model.SchemaType.BYTES
import io.toolisticon.avro.kotlin.model.SchemaType.STRING
import org.apache.avro.JsonProperties
import org.apache.avro.LogicalTypes
import org.apache.avro.compiler.specific.SpecificCompiler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ObjectPropertiesTest {

  @Test
  fun `no props means empty map`() {
    val schema = primitiveSchema(STRING)
    val props = ObjectProperties(schema)

    assertThat(props).isEmpty()
  }

  @Test
  fun `with properties and logicalType`() {
    val schema = primitiveSchema(
      type = BYTES,
      properties = ObjectProperties("xxx" to "value"),
      logicalType = LogicalTypes.uuid()
    )
    val props = schema.properties

    assertThat(props).isNotEmpty
    assertThat(props).doesNotContainKey("logicalType")
    assertThat(props.getValue<String>("xxx")).isEqualTo("value")
  }

  @Test
  fun `with properties map`() {
    val innerMap = ObjectProperties(
      "a" to 5,
      "b" to "foo"
    )

    val props = ObjectProperties(
      primitiveSchema(type = STRING, properties = ObjectProperties("zzz" to innerMap))
    )

    assertThat(props).isNotEmpty
    assertThat(props.getMap("zzz").getValue<Int>("a")).isEqualTo(5)

    val p = props.getValue<ObjectProperties>("zzz")

    assertThat(p.getValue<String>("b")).isEqualTo("foo")
  }

  @Test
  fun `javaAnnotations() - empty`() {
    assertThat(ObjectProperties().javaAnnotations).isEmpty()
  }

  @Test
  fun `javaAnnotations() - single annotation`() {
    val annotation = "foo.Bar(key=1)"
    assertThat(ObjectProperties(JavaAnnotation.PROPERTY_KEY to annotation).javaAnnotations).containsExactly(JavaAnnotation(annotation))
  }

  @Test
  fun `javaAnnotations() - multiple annotations`() {
    val annotation1 = "foo.Bar(key=1)"
    val annotation2 = "foo.HelloWorld"

    assertThat(ObjectProperties(JavaAnnotation.PROPERTY_KEY to listOf(annotation1, annotation2)).javaAnnotations)
      .containsExactlyInAnyOrder(
        JavaAnnotation(annotation1),
        JavaAnnotation(annotation2),
      )
  }

  @Test
  fun `avro compiler extracts same annotations from resource`() {
    val protocol = AvroKotlin.parseProtocol("org.apache.avro/protocol/simple.avpr")
    val compiler = SpecificCompiler(protocol.get())
    fun SpecificCompiler.javaAnnotationList(props: JsonProperties) = this.javaAnnotations(props).toList().map { JavaAnnotation(it) }

    val annotationsOnProtocol = compiler.javaAnnotationList(protocol.get())
    assertThat(protocol.properties.javaAnnotations)
      .containsExactlyInAnyOrder(*(annotationsOnProtocol.toTypedArray()))

    val annotationsOnTypeKind = compiler.javaAnnotationList(protocol.get().getType("Kind"))
    assertThat(protocol.getType(Name("Kind"))!!.properties.javaAnnotations)
      .containsExactlyInAnyOrder(*(annotationsOnTypeKind.toTypedArray()))

    val annotationsOnFieldName = compiler.javaAnnotationList(protocol.get().getType("TestRecord").getField("name"))
    val testRecord = protocol.getTypeAs<RecordType>(Name("TestRecord"))

    assertThat(testRecord.getField(Name("name"))!!.properties.javaAnnotations)
      .containsExactlyInAnyOrder(*(annotationsOnFieldName.toTypedArray()))
  }
}
