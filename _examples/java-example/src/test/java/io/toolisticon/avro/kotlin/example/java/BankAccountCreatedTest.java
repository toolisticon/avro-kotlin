package io.toolisticon.avro.kotlin.example.java;

import io.toolisticon.avro.kotlin.example.customerid.CustomerId;
import io.toolisticon.avro.kotlin.example.customerid.CustomerIdData;
import io.toolisticon.bank.BankAccountCreated;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.toolisticon.avro.kotlin.AvroKotlin.avroSchemaResolver;
import static io.toolisticon.avro.kotlin.codec.SpecificRecordCodec.specificRecordSingleObjectDecoder;
import static io.toolisticon.avro.kotlin.codec.SpecificRecordCodec.specificRecordSingleObjectEncoder;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountCreatedTest {

  @Test
  void encodeAndDecodeEventWithMoneyLogicalType() {
    var customerId = new CustomerIdData("1");
    final BankAccountCreated bankAccountCreated = BankAccountCreated.newBuilder()
      .setAccountId(UUID.randomUUID())
      .setCustomerId(customerId)
      .setInitialBalance(Money.of(100.123456, "EUR"))
      .build();
    final var resolver = avroSchemaResolver(BankAccountCreated.getClassSchema());

    final var encoded = specificRecordSingleObjectEncoder().encode(bankAccountCreated);

    final BankAccountCreated decoded = (BankAccountCreated) specificRecordSingleObjectDecoder(resolver).decode(encoded);

    assertThat(decoded.getAccountId()).isEqualTo(bankAccountCreated.getAccountId());
    assertThat(decoded.getCustomerId()).isEqualTo(customerId);
    assertThat(decoded.getInitialBalance()).isEqualTo(Money.of(100.12, "EUR"));
  }
}
