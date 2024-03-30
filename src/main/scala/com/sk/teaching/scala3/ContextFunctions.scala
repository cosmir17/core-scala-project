import scala.concurrent.{ExecutionContext, Future}

object ContextFunctions :
  val aList = List(2, 1, 3, 4)
  val sortedList = aList.sorted

  // CA : Context Argument
  // defs can take using clauses
  def methodWithoutCAs(nonContextArg: Int)(nonContextArg2: String): Long = ???

  def methodWithCAs(nonContextArg: Int)(using nonContextArg2: String): Long = ???

  val functionWithoutCAs = methodWithoutCAs
  // val func2 = methodWithCAs // doesn't work

  // context function
  val functionWithoutCAsNew: Int => String => Long = methodWithoutCAs
  val functionWithCAs: Int => String ?=> Long = methodWithCAs

  /*
    Use cases:
      - convert methods with using clauses to function values
      - Higher Order Function with function values taking given instances as arguments
      - requiring given instances at CALL SITE, not at DEFINITION
   */

  val incrementAsyncWithCA: Int => ExecutionContext ?=> Future[Int] =
    x => Future(x + 1)

  def main(args: Array[String]): Unit = {

  }