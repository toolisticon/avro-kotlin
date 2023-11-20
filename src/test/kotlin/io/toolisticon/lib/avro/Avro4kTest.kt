package io.toolisticon.lib.avro

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.AvroDefault
import io.toolisticon.lib.avro._data.DummyTypeData
import io.toolisticon.lib.avro._data.DummyTypeData.*
import io.toolisticon.lib.avro._data.NestedDummyData
import io.toolisticon.lib.avro._data.NestedMoneyData
import io.toolisticon.lib.avro.ext.BytesExt.toHexString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import lib.test.dummy.DummyType
import lib.test.dummy.NestedDummy
import lib.test.dummy.NestedMoney
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.message.BinaryMessageEncoder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.reflect.Constructor
import java.nio.ByteBuffer
import java.util.*

internal class Avro4kTest {

  @Test
  fun `serialize kotlin data to single object`() {
    val writerSchema = Avro.default.schema(NestedDummyData.serializer())

    val kotlinMessage = NestedDummyData(
      id = UUID.randomUUID(),
      money = NestedMoneyData(10.34.toBigDecimal(), "USD"),
      type = listOf(FOO)
    )

    val buffer : ByteBuffer = serializeSingleObject(kotlinMessage, NestedDummyData.serializer())

    println(buffer.toHexString())

    val deserializedJavaMessage = NestedDummy.createDecoder { _ -> writerSchema }.decode(buffer)


    with(deserializedJavaMessage) {
      assertThat(id).isEqualTo(kotlinMessage.id)
      assertThat(message).isEqualTo(kotlinMessage.message)
      assertThat(type).containsExactly(DummyType.FOO)
      assertThat(money.amount).isEqualTo(kotlinMessage.money.amount)
      assertThat(money.currencyCode).isEqualTo(kotlinMessage.money.currencyCode)
    }

  }

  @Test
  fun `print foo`() {
    val genericRecord = Avro.default.toRecord(serializer = Foo.serializer(), obj = Foo(name="Bar"))

    println(BinaryMessageEncoder<GenericRecord>(GenericData(), Avro.default.schema(Foo.serializer())).encode(genericRecord).array().toHexString())
  }

  @Serializable
  data class Foo(val name:String)

  fun serializeSingleObject(kotlinMessage: NestedDummyData, serializer: KSerializer<NestedDummyData>): ByteBuffer {
    val genericRecord = Avro.default.toRecord(serializer = serializer, obj = kotlinMessage)

    return BinaryMessageEncoder<GenericRecord>(GenericData(), Avro.default.schema(serializer)).encode(genericRecord)
  }

  @Test
  fun `deserialize kotlin data from java single object`() {
    val readerSchema = Avro.default.schema(NestedDummyData.serializer())

    val javaMessage = NestedDummy.newBuilder()
      .setId(UUID.randomUUID())
      .setMessage("some message")
      .setMoneyBuilder(
        NestedMoney.newBuilder()
          .setAmount(10.1.toBigDecimal().setScale(2))
          .setCurrencyCode("EUR")
      )
      .setType(listOf(DummyType.BAR, DummyType.FOO))
      .build()

    val javaMessageSingleObject = NestedDummy.getEncoder().encode(javaMessage)

    println(javaMessageSingleObject.toHexString())

    println(readerSchema)
    val deserializedKotlinData: NestedDummyData = deserializeSingleObject(readerSchema, javaMessageSingleObject)


    println(deserializedKotlinData)
    with(deserializedKotlinData) {
      assertThat(id).isEqualTo(javaMessage.id)
      assertThat(message).isEqualTo(javaMessage.message)
      assertThat(type).containsExactly(BAR, FOO)
      assertThat(money.amount).isEqualTo(javaMessage.money.amount)
      assertThat(money.currencyCode).isEqualTo(javaMessage.money.currencyCode)
    }

  }

  private fun deserializeSingleObject(
    readerSchema: Schema,
    javaMessageSingleObject: ByteBuffer?
  ): NestedDummyData {
    // TODO move to lib
    val record: GenericData.Record = BinaryMessageDecoder<GenericData.Record>(
      GenericData().apply {
        /*
                addLogicalTypeConversion(Conversions.UUIDConversion())
                addLogicalTypeConversion(Conversions.DecimalConversion())
        */
      },
      readerSchema
    ).apply {
      addSchema(NestedDummy.`SCHEMA$`)
    }.decode(javaMessageSingleObject)

    println("record: $record")
    val deserializedKotlinData: NestedDummyData = Avro.default.fromRecord(deserializer = NestedDummyData.serializer(), record)
    // REM this goes to avro4k lib
    // 20 GOTO 10
    // END TODO
    return deserializedKotlinData
  }
}
