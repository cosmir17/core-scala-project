object DependentFunctionType extends App:

// The below example is from https://docs.scala-lang.org/scala3/reference/new-types/dependent-function-types.html
  trait Entry:
    type Key
    val key: Key
  def extractKey(e: Entry): e.Key = e.key // a dependent method
//  val extractor: (e: Entry) => e.Key = extractKey // a dependent function value

//  Function1[Entry, Entry#Key]:
//    def apply(e: Entry): e.Key

/////////////////////////////////////////////////////////
//the below examples are from https://docs.scala-lang.org/scala3/book/types-dependent-function.html
  trait AnotherKey:
    type AnotherValue

  trait DB:
    def get(k: AnotherKey): Option[k.AnotherValue] // a dependent method

  object Name extends AnotherKey:
    type AnotherValue = String

  object Age extends AnotherKey:
    type AnotherValue = Int

  // a user of a DB
  def user(db: DB): Unit =
    for
      n <- db.get(Name)
      a <- db.get(Age)
    yield n + a

  // creating an instance of the DB and passing it to `user`
//  user(new DB {
//    def get(k: AnotherKey): Option[k.AnotherValue] = ???
//  })

//  trait DB:
//    def get(k: AnotherKey): Option[k.AnotherValue] // a dependent method type
//    val get2: (k: AnotherKey) => Option[k.AnotherValue] // a dependent function type


////////////////////////////////////////////////////

  trait Nums:
    // the type of numbers is left abstract
    type Num

    // some operations on numbers
    def lit(d: Double): Num
    def add(l: Num, r: Num): Num
    def mul(l: Num, r: Num): Num

//  type Prog = (n: Nums) => n.Num => n.Num
//  def derivative(input: Prog): Double = ???

//  derivative(new Prog {
//    def apply(nums: Nums)(x: nums.Num): nums.Num = x
//  })
//  derivative(new Prog {
//    def apply(nums: Nums)(x: nums.Num): nums.Num = nums.add(nums.lit(0.8), x)
//  })

//  val ex: Prog = nums => x => nums.add(nums.lit(0.8), x)

//  derivative { nums => x => x }
//  derivative { nums => x => nums.add(nums.lit(0.8), x) }

//////////////////////////////

  trait NumsDSL extends Nums:
    extension (x: Num)
      def +(y: Num) = add(x, y)
      def *(y: Num) = mul(x, y)

  def const(d: Double)(using n: Nums): n.Num = n.lit(d)

//  type Prog2 = (n: NumsDSL) ?=> n.Num => n.Num
  //                       ^^^
  //     prog is now a context function that implicitly
  //     assumes a NumsDSL in the calling context

//  def derivative2(input: Prog2): Double = ???

  // notice how we do not need to mention Nums in the examples below?
//  derivative { x => const(1.0) + x }
//  derivative { x => x * x + const(2.0) }