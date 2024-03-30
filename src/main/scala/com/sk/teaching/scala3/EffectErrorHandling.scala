import scala.util.{Failure, Success, Try}

import cats.effect.IO

object EffectErrorHandling {
  val aFailure: IO[Int] = IO.raiseError(new RuntimeException("a proper fail"))

  val dealWithIt = aFailure.handleErrorWith {
    case _: RuntimeException => IO.delay(println("I'm still here"))
    // add more cases
  }

  val effectAsEither: IO[Either[Throwable, Int]] = aFailure.attempt

  val resultAsString: IO[String] = aFailure.redeem(
    ex => s"FAIL: $ex",
    value => s"SUCCESS: $value"
  )

  val resultAsEffect: IO[Unit] = aFailure.redeemWith(
    ex => IO(println(s"FAIL: $ex")),
    value => IO(println(s"SUCCESS: $value")
    )
  ) //future.recoverWith

  val resultAsString2: IO[String] = effectAsEither.redeem(ex => s"FAIL: $ex", value => s"SUCCESS: $value")

  def option2IO[A](option: Option[A])(ifEmpty: Throwable): IO[A] =
    option match {
      case Some(value) => IO.pure(value)
      case None => IO.raiseError(ifEmpty)
    }

  def try2IO[A](aTry: Try[A]): IO[A] =
    aTry match {
      case Success(value) => IO.pure(value)
      case Failure(ex) => IO.raiseError(ex)
    }

  def either2IO[A](anEither: Either[Throwable, A]): IO[A] =
    anEither match {
      case Left(ex) => IO.raiseError(ex)
      case Right(value) => IO.pure(value)
    }

  def handleIOError[A](io: IO[A])(handler: Throwable => A): IO[A] =
    io.redeem(handler, identity)

  def handleIOErrorWith[A](io: IO[A])(handler: Throwable => IO[A]): IO[A] =
    io.redeemWith(handler, IO.pure)

  def main(args: Array[String]): Unit = {
    import cats.effect.unsafe.implicits.global
    resultAsEffect.unsafeRunSync()
  }
}
