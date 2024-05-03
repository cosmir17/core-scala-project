object Invariance:
  class ABC[A] //invariant

object Covariance1:
  class ABC[A]
  abstract class Animal:
    def name: String

  case class Dog(name: String) extends Animal

  case class Cat(name: String, colour: String) extends Animal

//  class ImmutableBox[+A](val content: A)
//  val catbox: ImmutableBox[Cat] = ImmutableBox[Cat](Cat("milton", "black"))
//  val animalBox2: ImmutableBox[Animal] = catbox // now this compiles

  class ImmutableBox[+A](val content: A)
  val catbox = ImmutableBox(Cat("milton", "black")) //the type of catbox automatically becomes ImmutableBox[Cat]
  val animalBox2: ImmutableBox[Animal] = catbox

// We say that ImmutableBox is covariant in A, and this is indicated by the + before the A.
// More formally, that gives us the following relationship: given some class Cov[+T],
// then if X is a subtype of Y, Cov[X] is a subtype of Cov[Y]. This allows us to make very useful and
// intuitive subtyping relationships using generics.

  abstract class Serializer[-A]:
    def serialize(a: A): String

  val animalSerializer: Serializer[Animal] = new Serializer[Animal]():
    def serialize(animal: Animal): String = s"""{ "name": "${animal.name}" }"""

  val catSerializer: Serializer[Cat] = animalSerializer
  catSerializer.serialize(Cat("tony", "orange"))
// We say that Serializer is contravariant in A, and this is indicated by the - before the A. A more general serializer
// is a subtype of a more specific serializer.
// More formally, that gives us the reverse relationship: given some class Contra[-T],
// then if Cat is a subtype of Animal, Contra[Animal] is a subtype of Contra[Cat].

//  abstract class Animal:
//    def name: String
//  case class Dog(name: String) extends Animal
//  case class Cat(name: String) extends Animal

  // 1 If `List` is covariant (`List[+A]`), we can also pass a `List[Dog]` or `List[Cat]` to `printCount`, increasing the method's reusability.
  def printCount(animalList: List[Animal]) = println(animalList.length)
  printCount(List(Dog("tom"), Dog("kate")))

  // 2. Type Safety Variance annotations enforce type safety by ensuring that you can't mistakenly put the wrong type into a data structure.
  class Container[+A](val element: A):
    def abc: String = ???
  val animalBox: Container[Animal] = new Container[Dog](Dog("tom")) // Compiles only if Container is covariant: Container[+A]

  // 3. API Design Designing a collection API that allows easy manipulation of data:
  trait Queue[+T]:
    def enqueue[U >: T](x: U): Queue[U] = ???
  // This allows for greater flexibility when using the `Queue`



object Covariance2:

  abstract class Animal:
    def name: String
  case class Dog(name: String) extends Animal
  case class Cat(name: String) extends Animal

  class Container[+A <: Animal](val element: A):
    def get: A = element // A method to showcase polymorphism using covariance
    def describe: String = s"This container holds a ${element.getClass.getSimpleName} named ${element.name}"

  val dogContainer = new Container[Dog](Dog("Rex"))
  val catContainer = new Container[Cat](Cat("Whiskers"))
  def printContainer(container: Container[Animal]): Unit = println(container.describe)

  printContainer(dogContainer)
  printContainer(catContainer)


object Contravariance:
  abstract class Animal:
    def sound: String
  case class Dog(sound: String = "Woof") extends Animal
  case class Cat(sound: String = "Meow") extends Animal
  class Printer[-A]:
    def print(a: A): String = s"Printing a ${a.getClass.getSimpleName}. It says: ${a.asInstanceOf[Animal].sound}"

  val animalPrinter: Printer[Animal] = new Printer[Animal]
  def printSound(printer: Printer[Dog], dog: Dog): Unit = println(printer.print(dog))

  printSound(animalPrinter, Dog())

  object Contravariance2:
    trait Function[-T, +R]:
      def apply(input: T): R
    abstract class Animal:
      def sound: String
    case class Dog(sound: String = "Woof") extends Animal
    case class Cat(sound: String = "Meow") extends Animal
  
    abstract class Sound:
      def asString: String
    case class DetailedSound(private val s: String) extends Sound:
      def asString: String = s"Sound is: $s"
  
    class AnimalSoundFunction extends Function[Animal, Sound]:
      def apply(animal: Animal): Sound = DetailedSound(animal.sound)
  
    def useFunction(func: Function[Dog, Sound], dog: Dog): Sound = func.apply(dog)
  
    val animalSoundFunc = new AnimalSoundFunction()
    val result: Sound = useFunction(animalSoundFunc, Dog())
  
    println(s"The sound is: ${result.asString}")