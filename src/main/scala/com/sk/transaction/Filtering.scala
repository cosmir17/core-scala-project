package com.sk.transaction

import cats.MonadError
import cats.instances.either._
import cats.instances.try_._
import squants.market.{Currency, ETH, NoSuchCurrencyException, defaultMoneyContext}

import scala.util.Try

case class Transaction(
                        userId: String,
                        amount: Int,
                        currency: String,
                        accountFrom: String,
                        accountTo: String
                      )

case class Result(
                   passed: Boolean,
                   code: Option[String] = None
                 )

case class BatchData(blackList: Set[String], results: Seq[Result]) { def this() = this(Set(), Seq()) }
case object BatchData { def apply() = new BatchData() }

object Filtering {
  type ErrorOr[A] = Either[Throwable, A]
  val meEither = MonadError[ErrorOr, Throwable]
  implicit val moneyContext = defaultMoneyContext

  private val invalidUserId = "INVALID_USER_ID"
  private val invalidAccountTO = "INVALID_ACCOUNT_TO"
  private val invalidCurrency = "INVALID_CURRENCY"
  private val invalidAmount = "INVALID_AMOUNT"

  private val launderError = "LAUNDER"
  private val launders = Seq("account-c", "account-f")
  case object LaunderException extends Exception(launderError)

  def filter(input: Transaction): Result =
    (for {
      userId          <- meEither.ensure(meEither.pure(input.userId))(new IllegalArgumentException(invalidUserId))(_ != "")
      accountTo       <- meEither.ensure(meEither.pure(input.accountTo))(new IllegalArgumentException(invalidAccountTO))(_ != "")
      launderFreeFrom <- meEither.ensure(meEither.pure(input.accountFrom))(LaunderException)(!launders.contains(_))
      launderFreeTo   <- meEither.ensure(meEither.pure(input.accountTo))(LaunderException)(!launders.contains(_))
      currency        <- MonadError[Try, Throwable].ensure(Currency(input.currency))(new IllegalArgumentException(invalidCurrency))(_ == ETH)
        .toEither match { case Left(NoSuchCurrencyException(_, _)) => Left(new IllegalArgumentException(invalidCurrency)); case r => r }
      amount          <- meEither.ensure(meEither.pure(input.amount))(new IllegalArgumentException(invalidAmount))(_ > 1000)
    } yield (userId, accountTo, launderFreeFrom, launderFreeTo, currency, amount)) match {
      case Left(aOrB @ (_: IllegalArgumentException | LaunderException))       => Result(false, Option(aOrB.getMessage))
      case Left(e)                            => println("Not recognised error"); Result(false, Option(e.getMessage))
      case Right(_)                                                            => Result(true)
    }

  def filter(input: Seq[Transaction]): Seq[Result] =
    input.foldLeft(BatchData())((prevIterations, now) => {
      val result = filter(now)
      val blackListed = prevIterations.blackList
      if (result.code.contains(launderError) | blackListed.contains(now.accountFrom) | blackListed.contains(now.accountTo)) {
        val blackListed2 = blackListed + now.accountFrom; val blackListed3 = blackListed2 + now.accountTo
        BatchData(blackListed3, prevIterations.results :+ Result(false, Some(launderError)))
      }
      else BatchData(blackListed, prevIterations.results :+ result)
    }).results
}