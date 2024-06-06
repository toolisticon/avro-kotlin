package io.toolisticon.kotlin.avro.generator.strategy

import io.toolisticon.kotlin.avro.AvroParser
import io.toolisticon.kotlin.avro.generator.TestFixtures.toReadableString
import io.toolisticon.kotlin.avro.generator.context.AvroKotlinGeneratorContextFactory
import io.toolisticon.kotlin.generation.builder.KotlinFileBuilder
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.apache.avro.SchemaBuilder
import org.junit.jupiter.api.Test

internal class DefaultGenerateDataClassStrategyTest {

  val factory = AvroKotlinGeneratorContextFactory()
  val strategy = factory.strategies.generateDataClassStrategy

  @Test
  fun `with decimal type`() {
    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("a.b.c.DecimalHolder")
        .doc("this is a holder.")
        .fields()
        .name("amount")
        .doc("this is a money amount")
        .type(LogicalTypes.decimal(6, 2).addToSchema(Schema.create(Schema.Type.BYTES)))
        .noDefault()
        .endRecord()
    )
    val ctx = factory.create(declaration)


    println(KotlinFileBuilder.builder(ctx.rootClassName).addType(strategy.generateDataClass(ctx, declaration.recordType)).build())
  }

  @Test
  fun `generate simple by strategy`() {
    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("a.b.c.Dee")
        .fields()
        .name("x")
        .type(Schema.createEnum("SomeEnum", "an enum", "a.b.c", listOf("HA", "HI", "HO")))
        .noDefault()
        .requiredString("foo")
        .endRecord()
    )


    val ctx = factory.create(declaration)

    val type = strategy.generateDataClass(ctx, declaration.recordType)
    println(KotlinFileBuilder.builder(ctx.rootClassName).addType(type).build())
  }

  @Test
  fun `nullable map type`() {
    val declaration = AvroParser().parseSchema(
      SchemaBuilder.record("xxx.yyy.Foo")
        .doc("This is the foo.")
        .fields()
        .name("aMap")
        .doc("this is a map with a nullable value")
        .type(
          Schema.createMap(
            Schema.createUnion(
              Schema.create(Schema.Type.NULL),
              LogicalTypes.uuid().addToSchema(Schema.create(Schema.Type.STRING))
            )
          )
        )
        .noDefault()
        .endRecord()
    )

    println(declaration.avroTypes.toReadableString())

    val ctx = factory.create(declaration)

    val type = strategy.generateDataClass(ctx, declaration.recordType)
    println(KotlinFileBuilder.builder(ctx.rootClassName).addType(type).build())

  }
}
