package EffectTechnics

import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.{Monad, Monoid}
import cats.implicits.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object EffectTechnics extends IOApp:

  given Logger[IO] = Slf4jLogger.getLogger[IO]
  def flatTapExample[F[_]: Monad: Logger](optionEffect: F[Option[Int]]): F[Option[Int]] =
    optionEffect.flatTap(opt => Logger[F].info(s"this is a log").whenA(opt.isEmpty))

  def foldMapExample[F[_]: Monad](listEffect: F[List[Int]]) : F[Int] =
    listEffect.map(_.foldMap(_ + 400))

  override def run(args: List[String]): IO[ExitCode] =
    for
     _ <- flatTapExample(IO(Some(5)))
     _ <- flatTapExample(IO(None))
     _ <- foldMapExample(IO(List(3,4,6)))
    yield ExitCode.Success