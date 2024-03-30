import scala.deriving._
import scala.compiletime.{erasedValue, summonInline}

object Deriving extends App:

  trait Show[A]:
    def show(x: A): String

//  object Show:
//    inline given derived[A](using m: Mirror.Of[A]): Show[A] = ???

  object Show:
    given Show[Int] = _.toString()

    given Show[String] = a => a

    def showElem(elem: Show[?])(x: Any): String =
      elem.asInstanceOf[Show[Any]].show(x)

    def iterator[A](p: A) = p.asInstanceOf[Product].productIterator

    def showSum[A](s: Mirror.SumOf[A], elems: => List[Show[?]]): Show[A] =
      (x: A) => showElem(elems(s.ordinal(x)))(x)

    def showProduct[A](p: Mirror.ProductOf[A], elems: => List[Show[?]]): Show[A] =
      (x: A) => iterator(x).zip(elems.iterator).map {
        case (x, elem) => showElem(elem)(x)
      }.mkString(", ")

    inline def summonAll[A <: Tuple]: List[Show[?]] =
      inline erasedValue[A] match
        case _: EmptyTuple => Nil
        case _: (t *: ts) => summonInline[Show[t]] :: summonAll[ts]

    inline def derived[A](using m: Mirror.Of[A]): Show[A] =
      lazy val elemInstances = summonAll[m.MirroredElemTypes]
        inline m match
          case s: Mirror.SumOf[A] => //sealed trait or enum
            showSum(s, elemInstances)
          case p: Mirror.ProductOf[A] => //case class or tuple
            showProduct(p, elemInstances)

  case class Person(age: Int, name: String)
    derives Show


//derives keyword creates an 'given Show[Person] = ???' instance

//COP project examples:
//case class Config(a: String, b: Int, d: Long, s: Tuple) derives ConfigReader
//=> given ConfigReader[Config]

//case class DomainType() derives CodeAsObject
//=> given CodeAsObject[DomainType]

  def printPerson(p: Person)(using pShow: Show[Person]) =
    println(pShow.show(p))

  printPerson(Person(34, "Robert"))
