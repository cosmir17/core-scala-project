package com.sk.orderbook

object ConsoleInputParser {
  case class ConsoleInput(fileName: String, tickSize: Double, bookDepth: Int)

  /**
    * translate an input command from terminal
    * @param input prog updates2.txt 10.0 3
    * @return returns a recognisable instruction object
    */
  def compute(input: String): ConsoleInput = {
    val tokens = input.split(" ").toList
    if(tokens.contains("exit")) System.exit(0)
    tokens.find(_ == "prog").getOrElse(throw new IllegalArgumentException("No command is present"))
    val doubleR = """(\d+\.\d+)""".r
    val fileNameOpt = tokens.find(_.endsWith("txt"))
    val tickOpt = doubleR.findFirstIn(input).map(parse[Double](_)(popDouble))
    val depthOpt = tokens.lastOption.map(parse[Int](_)(popInt))

    (fileNameOpt, tickOpt, depthOpt) match {
      case (Some(fileName), Some(tick), Some(depth)) => ConsoleInput(fileName, tick, depth)
      case (Some(_), Some(_), None) => throw new IllegalArgumentException("depth is not present")
      case (Some(_), None, Some(_)) => throw new IllegalArgumentException("tick is not present")
      case (Some(_), None, None) => throw new IllegalArgumentException("Both tick and depth are not present")
      case (None, _, _) => throw new IllegalArgumentException("file name can't be retrieved")
      case _ => throw new IllegalArgumentException("Unknown exception")
    }
  }

  //referenced from https://stackoverflow.com/questions/9542126/how-to-find-if-a-scala-string-is-parseable-as-a-double-or-not
  case class ParseOp[T](op: String => T)
  implicit val popDouble = ParseOp[Double](_.toDouble)
  implicit val popInt = ParseOp[Int](_.toInt)
  def parse[T: ParseOp](s: String) =
    try { implicitly[ParseOp[T]].op(s) } catch {
      case _: Throwable => throw new IllegalArgumentException("either tick or depth value is not present")
    }
}
