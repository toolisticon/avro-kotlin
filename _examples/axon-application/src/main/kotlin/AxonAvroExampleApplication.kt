package holi.bank

import holi.bank.BankAccountProtocol.BankAccountCreatedEvent
import holi.bank.BankAccountProtocol.CreateBankAccountCommand
import holi.bank.BankAccountProtocol.DepositMoneyCommand
import holi.bank.BankAccountProtocol.MoneyDepositedEvent
import holi.bank.BankAccountProtocol.MoneyWithdrawnEvent
import holi.bank.BankAccountProtocol.WithdrawMoneyCommand
import holi.bank.BankAccountProtocolCommandHandlerProtocol.CreateBankAccountCommandHandler
import holi.bank.BankAccountProtocolCommandHandlerProtocol.DepositMoneyCommandHandler
import holi.bank.BankAccountProtocolCommandHandlerProtocol.WithdrawMoneyCommandHandler
import holi.bank.BankAccountProtocolEventSourcingHandlerProtocol.CreateBankAccountEventSouringHandler
import holi.bank.BankAccountProtocolEventSourcingHandlerProtocol.DepositMoneyEventSouringHandler
import holi.bank.BankAccountProtocolEventSourcingHandlerProtocol.WithdrawMoneyEventSouringHandler
import holi.bank.BankAccountProtocolQueryGatewayExt.findAllMoneyTransfersForAccountId
import holi.bank.BankAccountProtocolQueryGatewayExt.findCurrentBalanceForAccountId
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

fun main() = runApplication<AxonAvroExampleApplication>().let { }

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

      val bankAccountId = UUID.randomUUID().toString()
      logger.info { "Created bank account id: $bankAccountId" }
      commandGateway.send<Void>(CreateBankAccountCommand(accountId = bankAccountId, initialBalance = 100)).join()

      logger.info { "Doing some money transfer: $bankAccountId" }
      commandGateway.send<Void>(DepositMoneyCommand(accountId = bankAccountId, amount = 99)).join()
      commandGateway.send<Void>(WithdrawMoneyCommand(accountId = bankAccountId, amount = 77)).join()

      // wait two secs
      Thread.sleep(2000)

      val currentBalance = queryGateway.findCurrentBalanceForAccountId(BankAccountProtocol.FindCurrentBalanceByAccountIdQuery(accountId = bankAccountId)).join()
      logger.info { "Current balance for account $bankAccountId: $currentBalance" }

      val transactions = queryGateway.findAllMoneyTransfersForAccountId(BankAccountProtocol.FindAllMoneyTransfersByAccountIdQuery(accountId = bankAccountId)).join()
      logger.info { "Transactions for account $bankAccountId: $transactions" }
    }
  }


  @Aggregate
  class BankAggregate :
    DepositMoneyCommandHandler,
    WithdrawMoneyCommandHandler,
    CreateBankAccountEventSouringHandler,
    DepositMoneyEventSouringHandler,
    WithdrawMoneyEventSouringHandler {

    @AggregateIdentifier
    private lateinit var accountId: String
    private var balance: Int = -1

    companion object : CreateBankAccountCommandHandler {

      // Workaround for the aggregate factory
      @JvmStatic
      @CommandHandler
      fun create(command: CreateBankAccountCommand): BankAggregate {
        return createBankAccount(command).let {
          BankAggregate()
        }
      }

      override fun createBankAccount(command: CreateBankAccountCommand) {
        AggregateLifecycle.apply(BankAccountCreatedEvent(command.accountId, command.initialBalance))
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
    BankAccountProtocolEventHandlers.BankAccountProtocolAllEventHandlers,
    BankAccountProtocolQueryProtocol.BankAccountProjection {

    private val balances: MutableMap<String, BankAccountProtocol.CurrentBalance> = mutableMapOf()
    private val transfers: MutableMap<String, MutableList<BankAccountProtocol.MoneyTransfer>> = mutableMapOf()

    override fun findCurrentBalanceForAccountId(query: BankAccountProtocol.FindCurrentBalanceByAccountIdQuery): Optional<BankAccountProtocol.CurrentBalance> {
      return Optional.ofNullable(balances[query.accountId])
    }

    override fun findAllMoneyTransfersForAccountId(query: BankAccountProtocol.FindAllMoneyTransfersByAccountIdQuery): List<BankAccountProtocol.MoneyTransfer> {
      return transfers[query.accountId] ?: emptyList()
    }

    override fun onBankAccountCreatedEvent(event: BankAccountCreatedEvent) {
      balances[event.accountId] = BankAccountProtocol.CurrentBalance(event.accountId, event.initialBalance)
      transfers[event.accountId] = mutableListOf()
    }

    override fun onMoneyDepositedEvent(event: MoneyDepositedEvent) {
      balances.computeIfPresent(event.accountId) { _, balance -> balance.copy(balance = balance.balance + event.amount) }
      transfers.computeIfPresent(event.accountId) { _, transfer -> transfer.apply { add(BankAccountProtocol.MoneyTransfer("deposit", event.amount)) } }
    }

    override fun onMoneyWithdrawnEvent(event: MoneyWithdrawnEvent) {
      balances.computeIfPresent(event.accountId) { _, balance -> balance.copy(balance = balance.balance - event.amount) }
      transfers.computeIfPresent(event.accountId) { _, transfer -> transfer.apply { add(BankAccountProtocol.MoneyTransfer("withdraw", event.amount)) } }
    }
  }
}
