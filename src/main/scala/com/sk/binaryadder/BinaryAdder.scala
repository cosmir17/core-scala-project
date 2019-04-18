package com.sk.binaryadder

object BinaryAdder {

  def addOne(binaryNumbers: Seq[Int]): Seq[Int] =
    (checkInput andThen calculate andThen removeFirstZeros)(binaryNumbers)

  private val checkInput = (input: Seq[Int]) =>
    input.map{case n@(1 | 0) => n; case _ => throw new IllegalArgumentException("containing a non-binary number(s)")}

  private val calculate = (binaryNumbers: Seq[Int]) =>
    binaryNumbers.foldRight[(Seq[Int], Boolean)]((Seq(), true))((n, folded) =>  //the boolean value => domino effect indicator
      (n, folded._1, folded._2) match {
        case (0, Seq(), _) => (Seq(1), false) //base case, 0 + 1 => 1
        case (1, Seq(), _) => (Seq(1, 0), true) //base case, 1 + 1 => 10
        case (0, _, true) => (folded._1, false)
        case (1, 1 +: tail, true) => (Seq(1, 0) ++ tail, true)
        case (_, _, false) => (n +: folded._1, false)
      })._1

  private val removeFirstZeros = (calculated: Seq[Int]) =>
    calculated.slice(calculated.takeWhile(_ == 0).size, calculated.size)
}