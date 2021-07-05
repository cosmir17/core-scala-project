package com.sk.supermarket

object Supermarket {

  sealed abstract class Item(val price: Int, val rule: Option[(Int, Int)])
  case object A extends Item(50, Some(3, 130))
  case object B extends Item(30, Some(2, 45))
  case object C extends Item(20, None)
  case object D extends Item(15, None)

  /**
    * calculate the total price of a list of items
    * @param items
    * @tparam real items
    * @return price
    */
  def calculateTotalPrice(items: List[Item]): Int = {
    def calculateSpecialPrice(`type`: Item, count: Int) = `type`.rule.map(t => {
      val d = count / t._1
      val remainder = count % t._1

      val specialP = d * t._2
      val normalP = remainder * `type`.price
      specialP + normalP
    }).getOrElse(`type`.price * count)

    items.groupBy(identity)
      .map { case (k, v) => (k, v.size) }
      .map { case (k, v) => calculateSpecialPrice(k, v) }
      .sum
  }
}
