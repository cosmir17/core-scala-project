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

object Filtering {
  type ErrorOr[A] = Either[Throwable, A]
  val meEither = MonadError[ErrorOr, Throwable]
  implicit val moneyContext = defaultMoneyContext

  val invalidUserId = "INVALID_USER_ID"
  val invalidAccountTO = "INVALID_ACCOUNT_TO"
  val invalidCurrency = "INVALID_CURRENCY"
  val invalidAmount = "INVALID_AMOUNT"

  def filter(input: Transaction): Result =
    (for {
      userId     <- meEither.ensure(meEither.pure(input.userId))(new IllegalArgumentException(invalidUserId))(_ != "")
      accountTo  <- meEither.ensure(meEither.pure(input.accountTo))(new IllegalArgumentException(invalidAccountTO))(_ != "")
      currency   <- MonadError[Try, Throwable].ensure(Currency(input.currency))(new IllegalArgumentException(invalidCurrency))(_ == ETH)
        .toEither match { case Left(NoSuchCurrencyException(_, _)) => Left(new IllegalArgumentException(invalidCurrency)); case r => r }
      amount     <- meEither.ensure(meEither.pure(input.amount))(new IllegalArgumentException(invalidAmount))(_ > 1000)
    } yield (userId, accountTo, currency, amount)) match {
      case Left(e: IllegalArgumentException)  => Result(false, Option(e.getMessage))
      case Left(e)                            => println("Not recognised error"); Result(false, Option(e.getMessage))
      case Right(_)                           => Result(true)
    }
}