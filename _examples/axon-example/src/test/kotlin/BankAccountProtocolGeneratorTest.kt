package io.holixon.axon.avro.generator

import io.holixon.axon.avro.generator.meta.FieldMetaData.Companion.fieldMetaData
import io.holixon.axon.avro.generator.meta.RecordMetaData.Companion.recordMetaData
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.value.Name
import mu.KLogging
import org.junit.jupiter.api.Test

class BankAccountProtocolGeneratorTest {
  companion object : KLogging()

  private val declaration = TestFixtures.parseProtocol("BankAccountProtocol.avpr")

  @Test
  fun `generate protocol`() {
    val file = TestFixtures.DEFAULT_GENERATOR.generate(declaration).single()

    logger.info { file.code }


    val record =  declaration.protocol.types.values.filterIsInstance<RecordType>().first()

    println(record.recordMetaData())
    println(record.getField(Name("accountId"))?.fieldMetaData())
  }
}
