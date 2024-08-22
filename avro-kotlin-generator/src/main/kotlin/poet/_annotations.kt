@file:OptIn(ExperimentalKotlinPoetApi::class)

package io.toolisticon.kotlin.avro.generator.poet

import com.github.avrokotlin.avro4k.AvroDecimal
import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.asTypeName
import io.toolisticon.kotlin.avro.generator.Avro4kSerializerKClass
import io.toolisticon.kotlin.avro.generator.AvroKotlinGenerator
import io.toolisticon.kotlin.generation.KotlinCodeGeneration
import io.toolisticon.kotlin.generation.KotlinCodeGeneration.buildAnnotation
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpec
import io.toolisticon.kotlin.generation.spec.KotlinAnnotationSpecSupplier
import io.toolisticon.kotlin.generation.support.CodeBlockArray
import jakarta.annotation.Generated
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import kotlin.reflect.KClass

/**
 * Creates a [KotlinAnnotationSpec] for `@Serializable(with=MyCustomSerializer::class)` as required for
 * logical types with custom serialization.
 */
@OptIn(ExperimentalKotlinPoetApi::class)
data class SerializableAnnotation(val serializerClass: Avro4kSerializerKClass? = null) : KotlinAnnotationSpecSupplier {
    override fun spec(): KotlinAnnotationSpec = buildAnnotation(Serializable::class) {
        if (serializerClass != null) {
            addKClassMember("with", serializerClass)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalKotlinPoetApi::class)
data class AvroDecimalAnnotation(val precision: Int = 0, val scale: Int = 0) : KotlinAnnotationSpecSupplier {
    override fun spec(): KotlinAnnotationSpec = buildAnnotation(AvroDecimal::class) {
        addNumberMember("precision", precision)
        addNumberMember("scale", scale)
    }
}

@OptIn(ExperimentalKotlinPoetApi::class)
data class SerialNameAnnotation(val name: String) : KotlinAnnotationSpecSupplier {
    override fun spec(): KotlinAnnotationSpec = buildAnnotation(SerialName::class) {
        addStringMember("value", name)
    }
}


// TODO: move to kotlin-code-generation when fixed
data class GeneratedAnnotation(
    val value: String = KotlinCodeGeneration::class.asTypeName().toString(),
    val date: Instant = Instant.now(),
    val comments: List<String> = emptyList()
) : KotlinAnnotationSpecSupplier {

    fun generator(type: KClass<*>) = copy(value = type.asTypeName().toString())
    fun date(instant: Instant) = copy(date = instant)
    fun comment(comment: Pair<String, String>) = copy(comments = this.comments + "${comment.first} = ${comment.second}")

    override fun spec(): KotlinAnnotationSpec = KotlinCodeGeneration.buildAnnotation(Generated::class) {
        // TODO: addMember should accept oceBlockSupplier to avoid call to build
        // TODO addMember for arrays
        addMember("value = %L", CodeBlockArray(KotlinCodeGeneration.format.FORMAT_STRING, listOf(value)).build())
        addStringMember("date", date.toString())

        if (comments.isNotEmpty()) {
            addStringMember("comments", comments.joinToString(separator = "; "))
        }
    }
}
