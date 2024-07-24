package io.toolisticon.kotlin.avro.example.java;

import io.toolisticon.example.bank.BankAccountCreated;
import io.toolisticon.kotlin.avro.repository.AvroSchemaResolverMap;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.toolisticon.kotlin.avro.codec.SpecificRecordCodec.specificRecordSingleObjectDecoder;
import static io.toolisticon.kotlin.avro.codec.SpecificRecordCodec.specificRecordSingleObjectEncoder;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountCreatedTest {

  @Test
  void encodeAndDecodeEventWithMoneyLogicalType() {
    //CustomerId customerId = CustomerId.of("1");
    final BankAccountCreated bankAccountCreated = BankAccountCreated.newBuilder()
      .setAccountId(UUID.randomUUID())
      .setCustomerId("1")
      .setInitialBalance(Money.of(100.123456, "EUR"))
      .build();
    final var resolver = new AvroSchemaResolverMap(BankAccountCreated.getClassSchema());

    final var encoded = specificRecordSingleObjectEncoder().encode(bankAccountCreated);

    final BankAccountCreated decoded = (BankAccountCreated) specificRecordSingleObjectDecoder(resolver).decode(encoded);

    assertThat(decoded.getAccountId()).isEqualTo(bankAccountCreated.getAccountId());
    assertThat(decoded.getCustomerId()).isEqualTo("1");
    assertThat(decoded.getInitialBalance()).isEqualTo(Money.of(100.12, "EUR"));
  }
}
