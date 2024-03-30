
object Scala3Practices extends App :

  /*
  * Intersection example is from
  * https://docs.scala-lang.org/scala3/book/types-intersection.html
  */
  trait Resettable:
    def reset(): Unit

  trait Growable[A]:
    def add(a: A): Unit

  def f(x: Resettable & Growable[String]): Unit =
    x.reset()
    x.add("first")

  trait Both[A] extends Resettable, Growable[A]
  def f2(x: Both[String]): Unit =
    x.reset()
    x.add("first")

  //    There is an important difference between the two alternatives of defining
  //    f: While both allow f to be called with instances of Both, only the former
  //    allows passing instances that are subtypes of Resettable and Growable[String],
  //    but not of Both[String]


  //  Union type
  final case class KeyDenied(message: String)
  final case class MissingPersonalInfo(message: String)
  final case class NetworkError(message: String)

  type KeyDeniedOrMissingPersonalInfo = KeyDenied | MissingPersonalInfo

  val example1: KeyDeniedOrMissingPersonalInfo = KeyDenied("Key is not valid")
  val example2: KeyDeniedOrMissingPersonalInfo = MissingPersonalInfo("It's missing height info")

  example2 match
    case KeyDenied(msg) => println(s"key denied: $msg")
    case MissingPersonalInfo(msg) => println(s"personal info missing: $msg")
