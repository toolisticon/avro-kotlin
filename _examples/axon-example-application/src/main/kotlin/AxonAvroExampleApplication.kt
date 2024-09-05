package holi.bank

import holi.bank.BankAccountContext.BankAccountCreatedEvent
import holi.bank.BankAccountContext.CreateBankAccountCommand
import holi.bank.BankAccountContext.CurrentBalance
import holi.bank.BankAccountContext.DepositMoneyCommand
import holi.bank.BankAccountContext.FindAllMoneyTransfersByAccountIdQuery
import holi.bank.BankAccountContext.FindCurrentBalanceByAccountIdQuery
import holi.bank.BankAccountContext.IllegalInitialBalance
import holi.bank.BankAccountContext.MoneyDepositedEvent
import holi.bank.BankAccountContext.MoneyTransfer
import holi.bank.BankAccountContext.MoneyWithdrawnEvent
import holi.bank.BankAccountContext.WithdrawMoneyCommand
import holi.bank.BankAccountContextCommandHandlers.BankAccountAggregateCommandHandlers
import holi.bank.BankAccountContextCommandHandlers.BankAccountAggregateCommandHandlers.BankAccountAggregateFactory
import holi.bank.BankAccountContextEventHandlers.BankAccountContextAllEventHandlers
import holi.bank.BankAccountContextEventSourcingHandlers.BankAccountAggregateSourcingHandlers
import holi.bank.BankAccountContextQueries.BankAccountProjectionQueries
import holi.bank.BankAccountContextQueryGatewayExt.findAllMoneyTransfersForAccountId
import holi.bank.BankAccountContextQueryGatewayExt.findCurrentBalanceForAccountId
import io.holixon.axon.avro.serializer.AvroSerializer
import io.holixon.axon.avro.serializer.spring.AvroSchemaScan
import io.holixon.axon.avro.serializer.spring.EnableAxonAvroSerializer
import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.json.JacksonSerializer
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.*

fun main() {
  System.setProperty("disable-axoniq-console-message", "true")
  runApplication<AxonAvroExampleApplication>()
}

@SpringBootApplication
class AxonAvroExampleApplication {

  @Configuration
  @EnableAxonAvroSerializer
  @AvroSchemaScan(
    basePackageClasses = [BankAccountContext::class] // commands, events, queries
  )
  class AvroSerializerConfiguration {

    @Bean
    @Primary
    fun defaultSerializer(): Serializer = JacksonSerializer.builder().build()

    @Bean
    @Qualifier("eventSerializer")
    fun eventSerializer(builder: AvroSerializer.Builder): Serializer = builder.build()

    @Bean
    @Qualifier("messageSerializer")
    fun messageSerializer(builder: AvroSerializer.Builder): Serializer = builder.build()
  }


  @Component
  class ExampleRunner(
    val commandGateway: CommandGateway,
    val queryGateway: QueryGateway
  ) {

    companion object : KLogging()

    @EventListener
    fun runExample(event: ApplicationStartedEvent) {
      logger.info {
        """
          ===============================================================================



                                         S H O W T I M E



          ================================================================================
        """.trimIndent()
      }

      val bankAccountId = UUID.randomUUID().toString()
      val createdAccountId = commandGateway.send<String>(CreateBankAccountCommand(accountId = bankAccountId, initialBalance = 100)).join()
      logger.info { "Created bank account id: $createdAccountId" }

      logger.info { "Doing some money transfer: $bankAccountId" }
      commandGateway.send<Void>(DepositMoneyCommand(accountId = bankAccountId, amount = 99)).join()
      commandGateway.send<Void>(WithdrawMoneyCommand(accountId = bankAccountId, amount = 77)).join()

      logger.info { "Taking a nap." }
      // wait two secs
      Thread.sleep(2000)

      val currentBalance = queryGateway.findCurrentBalanceForAccountId(FindCurrentBalanceByAccountIdQuery(accountId = bankAccountId)).join()
      logger.info { "Current balance for account $bankAccountId: $currentBalance" }

      val transactions = queryGateway.findAllMoneyTransfersForAccountId(FindAllMoneyTransfersByAccountIdQuery(accountId = bankAccountId)).join()
      logger.info { "Transactions for account $bankAccountId: $transactions" }

      logger.info {
        """
          ===============================================================================
                                           D O N E !
          ===============================================================================
        """.trimIndent()
      }
    }
  }


  @Aggregate
  class BankAccountAggregate : BankAccountAggregateCommandHandlers, BankAccountAggregateSourcingHandlers {

    @AggregateIdentifier
    private lateinit var accountId: String
    private var balance: Int = -1

    companion object : BankAccountAggregateFactory {

      private const val INITIAL_BALANCE_MIN = 20

      @JvmStatic // need to be static
      @CommandHandler // need to duplicate command handler
      @Throws(IllegalInitialBalance::class)
      override fun createBankAccount(command: CreateBankAccountCommand): BankAccountAggregate { // overwriting return type!
        if (command.initialBalance < INITIAL_BALANCE_MIN) {
          throw IllegalInitialBalance("Initial balance of the account must exceed ${INITIAL_BALANCE_MIN}, but it was ${command.initialBalance}.")
        }
        AggregateLifecycle.apply(BankAccountCreatedEvent(command.accountId, command.initialBalance))
        return BankAccountAggregate()
      }
    }

    override fun depositMoney(command: DepositMoneyCommand) {
      AggregateLifecycle.apply(MoneyDepositedEvent(this.accountId, command.amount))
    }

    override fun withdrawMoney(command: WithdrawMoneyCommand) {
      if (this.balance >= command.amount) {
        AggregateLifecycle.apply(MoneyWithdrawnEvent(this.accountId, command.amount))
      }
    }

    override fun onBankAccountCreatedEvent(event: BankAccountCreatedEvent) {
      this.accountId = event.accountId
      this.balance = event.initialBalance
    }


    override fun onMoneyDepositedEvent(event: MoneyDepositedEvent) {
      this.balance += event.amount
    }

    override fun onMoneyWithdrawnEvent(event: MoneyWithdrawnEvent) {
      this.balance -= event.amount
    }
  }

  @Component
  class BankAccountProjection : BankAccountContextAllEventHandlers, BankAccountProjectionQueries {

    private val balances: MutableMap<String, CurrentBalance> = mutableMapOf()
    private val transfers: MutableMap<String, MutableList<MoneyTransfer>> = mutableMapOf()

    override fun findCurrentBalanceForAccountId(query: FindCurrentBalanceByAccountIdQuery): Optional<CurrentBalance> {
      return Optional.ofNullable(balances[query.accountId])
    }

    override fun findAllMoneyTransfersForAccountId(query: FindAllMoneyTransfersByAccountIdQuery): List<MoneyTransfer> {
      return transfers[query.accountId] ?: emptyList()
    }

    override fun onBankAccountCreatedEvent(event: BankAccountCreatedEvent) {
      balances[event.accountId] = CurrentBalance(event.accountId, event.initialBalance)
      transfers[event.accountId] = mutableListOf()
    }

    override fun onMoneyDepositedEvent(event: MoneyDepositedEvent) {
      balances.computeIfPresent(event.accountId) { _, balance -> balance.copy(balance = balance.balance + event.amount) }
      transfers.computeIfPresent(event.accountId) { _, transfer -> transfer.apply { add(MoneyTransfer("deposit", event.amount)) } }
    }

    override fun onMoneyWithdrawnEvent(event: MoneyWithdrawnEvent) {
      balances.computeIfPresent(event.accountId) { _, balance -> balance.copy(balance = balance.balance - event.amount) }
      transfers.computeIfPresent(event.accountId) { _, transfer -> transfer.apply { add(MoneyTransfer("withdraw", event.amount)) } }
    }
  }
}
