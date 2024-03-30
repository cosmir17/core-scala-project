package com.sk.binaryadder

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.macros.union.*

object BinaryAdder:
  private type BinaryDigit = StrictEqual[1] | StrictEqual[0]

  def addOne(binaryNumbers: Seq[Int]): Seq[Int] =
    (checkInput andThen calculate andThen removeFirstZeros)(binaryNumbers)

  private val checkInput : Seq[Int] => Seq[Int :| BinaryDigit] =
    _.map(_.refineEither[BinaryDigit]).map:
      case Right(bd) => bd
      case Left(_) => throw new IllegalArgumentException(s"Input sequence contains non-binary number(s)")

  private val calculate = (binaryNumbers: Seq[Int :| BinaryDigit]) =>
    binaryNumbers.foldRight[(Seq[Int :| BinaryDigit], Boolean)]((Seq(), true))((n, folded) =>  //the boolean value => carry over indicator
      (n, folded._1, folded._2) match {
        case (0, Seq(), _) => (Seq(1), false) //base case, 0 + 1 => 1
        case (1, Seq(), _) => (Seq(1, 0), true) //base case, 1 + 1 => 10
        case (0, seq, true) => (seq, false) //base case 01 + 1 => 10
        case (1, 1 +: tail, true) => (Seq[Int :| BinaryDigit](1, 0) ++ tail, true)
        case (_, seq, _) => (n +: seq, false)
      })._1

  private val removeFirstZeros = (calculated: Seq[Int :| BinaryDigit]) =>
    calculated.slice(calculated.takeWhile(_ == 0).size, calculated.size)