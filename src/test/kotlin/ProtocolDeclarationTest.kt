package io.toolisticon.avro.kotlin

import io.toolisticon.avro.kotlin.AvroKotlin.namespace
import io.toolisticon.avro.kotlin.TestFixtures.resourceUrl
import io.toolisticon.avro.kotlin.ktx.createRecord
import org.apache.avro.Protocol
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.junit.jupiter.api.Test

// FIXME: remove/refactor!
//@Disabled("dead end")
internal class ProtocolDeclarationTest {

  @Test
  fun `construct declaration from resource`() {
    val declaration = TestFixtures.loadProtocolDeclaration("protocol/DummyProtocol.avpr")

    println(declaration)


  }

  @Test
  fun name() {
    val p = Protocol.parse(resourceUrl("protocol/DummyProtocol.avpr").readText())

    val message = p.messages["queryDummy"]!!

    val cp = message.createRecord(namespace(p))

    println(message.request)
    println(cp)

  }

  @Test
  fun `modify schema name`() {

    val s: Schema = SchemaBuilder.record("foo.Bar").fields()
      .requiredString("xxx")
      .endRecord()


    println(s)
  }
}
