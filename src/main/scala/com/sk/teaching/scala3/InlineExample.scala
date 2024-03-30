package InlineExample

object InlineExample1 :

  /*
  * inline scala documentation : https://docs.scala-lang.org/scala3/guides/macros/inline.html
  * */
  import scala.compiletime.error

  trait Permitted[T]()

  def accept[T](value: T)(using Permitted[T]): Unit = println(s"$value is allowed")

  given Permitted[Int]()

  given Permitted[String]()

  inline given Permitted[Double] = error("No doubles, please!")

  accept(1)
  accept("two")
//  accept(3.0) this will error here because it's using the double type


object InlineExample2:
  trait Debug[T]:
    def debugMethod(value: T): String
  given Debug[Int] = int => s"Int($int)"

  given Debug[Double] = double => s"Double($double)"

  given Debug[Char] = ch => s"'$ch'"

  given Debug[String] = string => s"$string"

//  extension[T] (value: T)(using debug: Debug[T]) def debugExtension: String = debug.debugMethod(value)
//  def log[T](value: T)(using Debug[T]): Unit = println(value.debugExtension)

//  def main(args: Array[String]) =
//    log(math.Pi)
//    log(42)
//    log('x')
//    log("Hello world")

  import scala.compiletime.summonInline
  inline def log[T](value: T): Unit = println(summonInline[Debug[T]].debugMethod(value)) //this code replaces the extension line and the one below

  /*
  * compile time summon API scala documentation: https://docs.scala-lang.org/scala3/guides/macros/compiletime.html
  * */

  def main(args: Array[String]) =
    log(42)
    log('x')
    log("Hello world")

