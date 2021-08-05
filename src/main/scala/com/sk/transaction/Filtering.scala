package com.sk.transaction

import cats.MonadError
import cats.data.State
import cats.implicits._
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

case class BatchData(blackList: Set[String], results: Seq[Result])
object BatchData { val empty = new BatchData(Set(), Seq()) }

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

  /**
    * Single validation operation
    * @param Transaction object
    * @return Result object
    */
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

  /**
    * Batch validation operation
    * @param a list of Transactions
    * @return a sequence of Results
    */
  def filter(input: Seq[Transaction]): Seq[Result] = {
    val computed = input.toList.traverse_ { updateBlackListAndProduceResult } .runS(BatchData.empty).value
    printIfLaundersFound(computed)
    computed.results
  }

  /**
    * logging is a side effect. Cats effect is to be used for thoroughness and testing ideally.
    * However, it seems ok not to use it for this purpose as this application is not under load and not running for 24hs/365ds.
    * also, the timing when they get printed is not a crucial matter for this application
    *
    * for enterprise-level application use, CE can be used to test the logging. Please refer to my other project.
    * https://github.com/cosmir17/cats-effect3-projects/blob/master/video-asset-handler/modules/tests/src/test/scala/modules/HashHandlerCompareTest.scala
    * @param BatchData launders and Results
    */
  private def printIfLaundersFound(laundersAndResults: BatchData): Unit =
    (launders.sorted, laundersAndResults.blackList.diff(launders.toSet).toSeq) match {
      case (Seq(), Seq()) =>
      case (original, Seq()) =>
        println(original.mkString("The original known launders: ", ", ", ""))
      case (original, discovered) =>
        println(original.mkString("The original known launders: ", ", ", ""))
        println(discovered.mkString("newly discovered suspected launders: ", ", ", ""))
    }

  /**
    * This is for the folding operation in the Batch filter method.
    * This takes a list of (launders and Results) and a single transaction object, yield a Result for the supplied transaction data.
    * This result gets appended inside the provided BatchData object as well as newly discovered associated launders
    * for the next iteration of the folding process.
    * @param laundersAndResults
    * @param transaction
    * @return a newly appended laundersAndResults object
    */
  private def updateBlackListAndProduceResult(transaction: Transaction): State[BatchData, Unit] = State.modify {
    bdState => {
      val result = filter(transaction)
      val blackListed = bdState.blackList
      if (result.code.contains(launderError) | blackListed.contains(transaction.accountFrom) | blackListed.contains(transaction.accountTo)) {
        val blackListed2 = blackListed + transaction.accountFrom;
        val blackListed3 = blackListed2 + transaction.accountTo
        bdState.copy(blackListed3, bdState.results :+ Result(false, Some(launderError)))
      }
      else bdState.copy(results = bdState.results :+ result)
    }
  }

}