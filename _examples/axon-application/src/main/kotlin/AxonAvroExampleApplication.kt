package holi.bank

import holi.bank.BankAccountContext.BankAccountCreatedEvent
import holi.bank.BankAccountContext.CreateBankAccountCommand
import holi.bank.BankAccountContext.DepositMoneyCommand
import holi.bank.BankAccountContext.MoneyDepositedEvent
import holi.bank.BankAccountContext.MoneyWithdrawnEvent
import holi.bank.BankAccountContext.WithdrawMoneyCommand
import holi.bank.BankAccountContextCommandHandlerProtocol.BankAccountAggregateSpec
import holi.bank.BankAccountContextCommandHandlerProtocol.BankAccountAggregateSpec.BankAccountAggregateSpecFactory
import holi.bank.BankAccountContextEventSourcingHandlerProtocol.BankAccountAggregateSpecSourcingHandler
import holi.bank.BankAccountContextQueryGatewayExt.findAllMoneyTransfersForAccountId
import holi.bank.BankAccountContextQueryGatewayExt.findCurrentBalanceForAccountId
import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.*

fun main() {
  System.setProperty("disable-axoniq-console-message", "true")
  runApplication<AxonAvroExampleApplication>().let { }
}

@SpringBootApplication
class AxonAvroExampleApplication {

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



          .                              S H O W T I M E



          ================================================================================
        """.trimIndent()
      }

      val bankAccountId = UUID.randomUUID().toString()
      logger.info { "Created bank account id: $bankAccountId" }
      commandGateway.send<Void>(CreateBankAccountCommand(accountId = bankAccountId, initialBalance = 100)).join()

      logger.info { "Doing some money transfer: $bankAccountId" }
      commandGateway.send<Void>(DepositMoneyCommand(accountId = bankAccountId, amount = 99)).join()
      commandGateway.send<Void>(WithdrawMoneyCommand(accountId = bankAccountId, amount = 77)).join()

      logger.info { "Taking a nap." }
      // wait two secs
      Thread.sleep(2000)

      val currentBalance = queryGateway.findCurrentBalanceForAccountId(BankAccountContext.FindCurrentBalanceByAccountIdQuery(accountId = bankAccountId)).join()
      logger.info { "Current balance for account $bankAccountId: $currentBalance" }

      val transactions = queryGateway.findAllMoneyTransfersForAccountId(BankAccountContext.FindAllMoneyTransfersByAccountIdQuery(accountId = bankAccountId)).join()
      logger.info { "Transactions for account $bankAccountId: $transactions" }
    }
  }


  @Aggregate
  class BankAccountAggregate :
    BankAccountAggregateSpec,
    BankAccountAggregateSpecSourcingHandler {

    @AggregateIdentifier
    private lateinit var accountId: String
    private var balance: Int = -1

    companion object : BankAccountAggregateSpecFactory {
      @JvmStatic // need to be static
      @CommandHandler // need to duplicate command handler
      override fun createBankAccount(command: CreateBankAccountCommand): BankAccountAggregate { // overwriting return type!
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
  class BankAccountProjection :
    BankAccountContextEventHandlers.BankAccountContextAllEventHandlers,
    BankAccountContextQueryProtocol.BankAccountProjectionSpec {

    private val balances: MutableMap<String, BankAccountContext.CurrentBalance> = mutableMapOf()
    private val transfers: MutableMap<String, MutableList<BankAccountContext.MoneyTransfer>> = mutableMapOf()

    override fun findCurrentBalanceForAccountId(query: BankAccountContext.FindCurrentBalanceByAccountIdQuery): Optional<BankAccountContext.CurrentBalance> {
      return Optional.ofNullable(balances[query.accountId])
    }

    override fun findAllMoneyTransfersForAccountId(query: BankAccountContext.FindAllMoneyTransfersByAccountIdQuery): List<BankAccountContext.MoneyTransfer> {
      return transfers[query.accountId] ?: emptyList()
    }

    override fun onBankAccountCreatedEvent(event: BankAccountCreatedEvent) {
      balances[event.accountId] = BankAccountContext.CurrentBalance(event.accountId, event.initialBalance)
      transfers[event.accountId] = mutableListOf()
    }

    override fun onMoneyDepositedEvent(event: MoneyDepositedEvent) {
      balances.computeIfPresent(event.accountId) { _, balance -> balance.copy(balance = balance.balance + event.amount) }
      transfers.computeIfPresent(event.accountId) { _, transfer -> transfer.apply { add(BankAccountContext.MoneyTransfer("deposit", event.amount)) } }
    }

    override fun onMoneyWithdrawnEvent(event: MoneyWithdrawnEvent) {
      balances.computeIfPresent(event.accountId) { _, balance -> balance.copy(balance = balance.balance - event.amount) }
      transfers.computeIfPresent(event.accountId) { _, transfer -> transfer.apply { add(BankAccountContext.MoneyTransfer("withdraw", event.amount)) } }
    }
  }
}
