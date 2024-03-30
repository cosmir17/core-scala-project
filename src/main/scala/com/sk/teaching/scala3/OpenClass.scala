import OpenClass.Animal

object OpenClass:
  open class Animal()

open class Animal2()

trait DDD

abstract class AAA()

class EEE extends DDD

class FFF extends AAA()

class Rabbit() extends Animal


//three cases of using class
class GGG()

//1. intend to extend it
open class YYY():

  //asdlfkajsdflkasjdklfajdsklfjasdklfjaslkfjaskldf
  def s(): Int = 4

class CCC extends YYY:
  override def s(): Int = ???

//2. We don't want to extend it for security and design reasons
final class RRR()

//3. We don't know
class ZZZ()
