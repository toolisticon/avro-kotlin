package io.toolisticon.kotlin.avro.value.property

import io.toolisticon.kotlin.avro.AvroKotlin
import io.toolisticon.kotlin.avro.TestFixtures.emptyStringMap
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.value.CanonicalName
import io.toolisticon.kotlin.avro.value.Name
import io.toolisticon.kotlin.avro.value.Namespace
import io.toolisticon.kotlin.avro.value.ObjectProperties
import org.apache.avro.JsonProperties
import org.apache.avro.compiler.specific.SpecificCompiler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource


internal class JavaAnnotationPropertyTest {

    enum class CreatePropertyFromAnnotationStringParameter(
        val annotationString: String,
        val canonicalName: CanonicalName,
        val members: Map<String, String>
    ) {
        FOO("Foo", Namespace.EMPTY + Name("Foo"), emptyStringMap),
        FOO_WITH_MEMBER(
            "bar.Foo(value = \"hello\", sum=8",
            Namespace("bar") + Name("Foo"),
            mapOf("value" to "\"hello\"", "sum" to "8")
        ),
        GENERATED(
            """javax.annotation.Generated("avro")""",
            Namespace("javax.annotation") + Name("Generated"),
            mapOf("value" to "\"avro\"")
        )
    }

    @ParameterizedTest
    @EnumSource(value = CreatePropertyFromAnnotationStringParameter::class)
    fun `create property from annotationString`(parameter: CreatePropertyFromAnnotationStringParameter) {
        val annotationProperty = JavaAnnotationProperty(parameter.annotationString)

        assertThat(annotationProperty.canonicalName).isEqualTo(parameter.canonicalName)
        assertThat(annotationProperty.members).containsAllEntriesOf(parameter.members)
    }

    @Test
    fun `javaAnnotations() - empty`() {
        assertThat(ObjectProperties().javaAnnotations).isEmpty()
    }

    @Test
    fun `javaAnnotations() - single annotation`() {
        val annotation = "foo.Bar(key=1)"
        assertThat(ObjectProperties(JavaAnnotationProperty.PROPERTY_KEY to annotation).javaAnnotations).containsExactly(
            JavaAnnotationProperty(annotation)
        )
    }

    @Test
    fun `javaAnnotations() - multiple annotations`() {
        val annotation1 = "foo.Bar(key=1)"
        val annotation2 = "foo.HelloWorld"

        assertThat(
            ObjectProperties(
                JavaAnnotationProperty.PROPERTY_KEY to listOf(
                    annotation1,
                    annotation2
                )
            ).javaAnnotations
        )
            .containsExactlyInAnyOrder(
                JavaAnnotationProperty(annotation1),
                JavaAnnotationProperty(annotation2),
            )
    }

    @Test
    fun `avro compiler extracts same annotations from resource`() {
        val protocol = AvroKotlin.parseProtocol("org.apache.avro/protocol/simple.avpr")
        val compiler = SpecificCompiler(protocol.get())
        fun SpecificCompiler.javaAnnotationList(props: JsonProperties) =
            this.javaAnnotations(props).toList().map { JavaAnnotationProperty(it) }

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

    @ParameterizedTest
    @CsvSource(
        value = [
            """bar.Foo("hello" ,  sum =  8);JavaAnnotationProperty('@bar.Foo(value="hello", sum=8)')""",
            """io.toolisticon.avro.avro4k.generator.processor.WorldField;JavaAnnotationProperty('@io.toolisticon.avro.avro4k.generator.processor.WorldField()')""",
            """io.toolisticon.avro.avro4k.generator.processor.WorldField();JavaAnnotationProperty('@io.toolisticon.avro.avro4k.generator.processor.WorldField()')""",
        ], delimiter = ';'
    )
    fun `verify toString`(annotationString: String, expectedToString: String) {
        val property = JavaAnnotationProperty(annotationString)
        assertThat(property).hasToString(expectedToString)
    }
}
