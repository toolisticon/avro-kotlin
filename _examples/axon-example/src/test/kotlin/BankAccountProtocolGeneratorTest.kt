package io.holixon.axon.avro.generator

import io.holixon.axon.avro.generator.meta.FieldMetaData.Companion.fieldMetaData
import io.holixon.axon.avro.generator.meta.RecordMetaData.Companion.recordMetaData
import io.toolisticon.kotlin.avro.declaration.ProtocolDeclaration
import io.toolisticon.kotlin.avro.model.RecordType
import io.toolisticon.kotlin.avro.value.Name
import mu.KLogging
import org.junit.jupiter.api.Test

class BankAccountProtocolGeneratorTest {
  companion object : KLogging()

  private val declaration: ProtocolDeclaration = TestFixtures.parseProtocol("BankAccountProtocol.avpr")

  @Test
  fun `generate protocol`() {
    val files = TestFixtures.DEFAULT_GENERATOR.generate(declaration)

    val record =  declaration.protocol.types.values.filterIsInstance<RecordType>().first()

    println(record.recordMetaData())
    println(record.getField(Name("accountId"))?.fieldMetaData())

    files.forEach{
      logger.info { "===== FILE: ${it.fqn}\n${it.code}\n" }
    }
  }
}
