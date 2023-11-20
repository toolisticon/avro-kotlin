package io.toolisticon.lib.avro

import io.toolisticon.lib.avro.ext.BytesExt.toHexString
import lib.test.dummy.DummyType
import lib.test.dummy.NestedDummy
import lib.test.dummy.NestedMoney
import lib.test.event.BankAccountCreated
import org.apache.avro.Conversions.DecimalConversion
import org.apache.avro.Conversions.UUIDConversion
import org.apache.avro.Schema
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericData.Record
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.io.DecoderFactory
import org.apache.avro.message.BinaryMessageDecoder
import org.apache.avro.specific.SpecificData
import org.junit.jupiter.api.Test
import java.util.*

internal class DataClassSerializationTest {


  val schema = Schema.Parser().parse("""
      {
        "namespace": "io.toolisticon.lib.avro",
        "name": "BankAccountCreatedData",
        "type": "record",
        "fields": [
          {
            "name": "initialBalance",
            "type": {
              "type": "int"
            }
          },
          {
            "name": "bankAccountId",
            "type": {
              "type": "string",
              "logicalType": "uuid"
            }
          }
        ]
      }

    """.trimIndent())


  @Test
  fun `deserialize to generic`() {
    val evt = BankAccountCreated.newBuilder()
      .setBankAccountId(UUID.randomUUID())
      .setInitialBalance(100)
      .build()

    val buffer = BankAccountCreated.getEncoder().encode(evt)

    println(buffer.toHexString())

    GenericData().apply {
      addLogicalTypeConversion(UUIDConversion())
    }

    val record: Record = BinaryMessageDecoder<Record>(
      GenericData().apply {
        //addLogicalTypeConversion(UUIDConversion())
      },
      schema
    ).apply {
      addSchema(BankAccountCreated.`SCHEMA$`)
    }.decode(buffer)

    println(record)
  }

  @Test
  fun `deserialize to data class`() {
//    val evt = BankAccountCreated.newBuilder()
//      .setBankAccountId(UUID.randomUUID())
//      .setInitialBalance(100)
//      .build()
//
//    val buffer = BankAccountCreated.getEncoder().encode(evt)
////    val dec = BankAccountCreated.getDecoder().decode(buffer)
//
//    val specificData = SpecificData().apply {
//      addLogicalTypeConversion(UUIDConversion())
//    }
//
//
//
//    val decoder = BinaryMessageDecoder<BankAccountCreatedData>(specificData, schema).apply {
//      this.addSchema(BankAccountCreated.`SCHEMA$`)
//    }
//
//    val data = decoder.decode(buffer)
//
//
//    val byte = buffer.toHexString()
//
//    println(byte)
//    println(data)
  }

  @Test
  fun `nested dummy example`() {
    val evt = NestedDummy.newBuilder()
      .setId(UUID.randomUUID())
      .setMessage("some message")
      .setMoneyBuilder(NestedMoney.newBuilder()
        .setAmount(10.1.toBigDecimal())
        .setCurrencyCode("EUR")
      )
      .setType(listOf(DummyType.BAR, DummyType.FOO))
      .build()

    val buffer = NestedDummy.getEncoder().encode(evt)

    println(buffer.toHexString())

    val record: Record = BinaryMessageDecoder<Record>(
      GenericData().apply {
        addLogicalTypeConversion(UUIDConversion())
        addLogicalTypeConversion(DecimalConversion())
      },
      nestedDummy
    ).apply {
      addSchema(NestedDummy.`SCHEMA$`)
    }.decode(buffer)

    println(record)
    println(record)



  }


  val nestedDummy = Schema.Parser().parse("""
    {
      "namespace": "io.toolisticon.lib.avro._data",
      "name": "NestedDummyData",
      "doc": "trying some more advanced schema ops",
      "type": "record",
      "fields": [
        {
          "name": "type",
          "type": {
            "type": "array",
            "items": {
              "type": "enum",
              "name": "DummyType",
              "symbols": [
                "FOO",
                "BAR"
              ]
            }
          }
        },
        {
          "name": "message",
          "doc": "Optional message for continuation error.",
          "type": [
            "null",
            "string"
          ],
          "default": null
        },
        {
          "name": "id",
          "type": {
            "type": "string",
            "logicalType": "uuid"
          }
        },
        {
          "name": "money",
          "type": {
            "name": "NestedMoney",
            "type": "record",
            "fields": [
              {
                "name": "amount",
                "doc": "money amount as 123.45",
                "type": {
                  "type": "bytes",
                  "logicalType": "decimal",
                  "precision": 10,
                  "scale": 2
                }
              },
              {
                "name": "currencyCode",
                "type": "string",
                "doc": "3-letter uppercase currency code"
              }
            ]
          }
        }
      ]
    }
  """.trimIndent())
}
