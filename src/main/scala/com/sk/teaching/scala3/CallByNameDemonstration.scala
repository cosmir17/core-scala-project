object CallByNameDemonstration extends App:

  //https://stackoverflow.com/questions/13337338/call-by-name-vs-call-by-value-in-scala-clarification-needed
  def something(): Int = {
    println("calling something")  //a side-effect
    1 // return value
  }

  def callByValue(x: Int) = {
    println("x1=" + x)
    println("x2=" + x)
  }

  def callByName(x: => Int) = { //similar to () => Int, the differences
    println("x1=" + x)
    println("x2=" + x)
  }

//  callByValue(something())

//  callByName(something()) //similar to () => {something()}

//*>
//>>

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//  https://stackoverflow.com/questions/19948598/by-name-parameter-vs-anonymous-function
  def func1(a: => Int) = {
    val b = a // b is of type Int, and it`s value is the result of evaluation of a
  }

  def func2(a: () => Int) = {
    val b = a // b is of type Function0 (is a reference to function a)
  }
//
  def generateInt(): Int = ???

  def byFunc(a: () => Int) = ???

  def byName(a: => Int) = ???

  // you can pass method without generating additional anonymous function
  byFunc(generateInt)

  // but both of the below are the same
  // i.e. additional anonymous function is generated
  byName(generateInt())
//  byName(() => generateInt())
//
  def measured(block: => Unit): Long = {
    val startTime = System.currentTimeMillis()
    block
    System.currentTimeMillis() - startTime
  }

  val timeTaken = measured {
    // any code here you like to measure
    // written just as there were no "measured" around
  }
//

