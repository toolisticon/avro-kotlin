package io.toolisticon.avro.kotlin.example.java;

import io.toolisticon.bank.BankAccountCreated;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import static io.toolisticon.avro.kotlin.AvroKotlin.avroSchemaResolver;
import static io.toolisticon.avro.kotlin.codec.SpecificRecordCodec.specificRecordSingleObjectDecoder;
import static io.toolisticon.avro.kotlin.codec.SpecificRecordCodec.specificRecordSingleObjectEncoder;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountCreatedTest {

  @Test
  void encodeAndDecodeEventWithMoneyLogicalType() {
    final BankAccountCreated bankAccountCreated = BankAccountCreated.newBuilder()
      .setId("1")
      .setAmount(Money.of(100.123456, "EUR"))
      .build();
    final var resolver = avroSchemaResolver(BankAccountCreated.getClassSchema());

    final var encoded = specificRecordSingleObjectEncoder().encode(bankAccountCreated);

    final BankAccountCreated decoded = (BankAccountCreated) specificRecordSingleObjectDecoder(resolver).decode(encoded);

    assertThat(decoded.getId()).isEqualTo(bankAccountCreated.getId());
    assertThat(decoded.getAmount()).isEqualTo(Money.of(100.12, "EUR"));
  }
}
