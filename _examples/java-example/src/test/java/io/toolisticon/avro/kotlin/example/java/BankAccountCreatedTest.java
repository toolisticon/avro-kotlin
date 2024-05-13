package io.toolisticon.avro.kotlin.example.java;

import static org.assertj.core.api.Assertions.assertThat;

import io.toolisticon.avro.kotlin.AvroKotlin;
import io.toolisticon.avro.kotlin.AvroSchemaResolver;
import io.toolisticon.avro.kotlin.AvroSchemaResolverKt;
import io.toolisticon.avro.kotlin.codec.SpecificRecordCodec;
import io.toolisticon.avro.kotlin.model.wrapper.AvroSchema;
import io.toolisticon.bank.BankAccountCreated;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class BankAccountCreatedTest {

  @Test
  void encodeAndDecodeEventWithMoneyLogicalType() throws IOException {
    final BankAccountCreated bankAccountCreated = BankAccountCreated.newBuilder()
      .setId("1")
      .setAmount(Money.of(100.123456, "EUR"))
      .build();
//    AvroSchema schema = new AvroSchema(BankAccountCreated.getClassSchema());

    final var encoded = SpecificRecordCodec.specificRecordSingleObjectEncoder().encode(bankAccountCreated);

    final ByteBuffer bytes = BankAccountCreated.getEncoder().encode(bankAccountCreated);

    final BankAccountCreated decoded = null; // FIXME:SpecificRecordCodec.specificRecordSingleObjectDecoder(AvroSchemaResolverKt.avroSchemaResolver());

    assertThat(decoded.getId()).isEqualTo(bankAccountCreated.getId());
    assertThat(decoded.getAmount()).isEqualTo(Money.of(100.12, "EUR"));
  }
}
