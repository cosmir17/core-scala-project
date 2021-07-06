package com.sk.supermarket

object Supermarket {
  case class Promotion(count: Int, newPrice: Int)

  sealed abstract class Item(val price: Int, val rule: Option[Promotion])
  case object A extends Item(50, Some(Promotion(3, 130)))
  case object B extends Item(30, Some(Promotion(2, 45)))
  case object C extends Item(20, None)
  case object D extends Item(15, None)

  /**
    * calculate the total price of a list of items
    * @param items
    * @tparam real items
    * @return price
    */
  def calculateTotalPrice(items: List[Item]): Int = items
      .groupBy(identity)
      .map { case (k, v) => (k, v.size) }
      .map { case (k, v) => applyPromotion(k, v) }
      .sum

  private def applyPromotion(item: Item, count: Int) = item.rule.map(t => {
    val d = count / t.count
    val remainder = count % t.count

    val specialP = d * t.newPrice
    val normalP = remainder * item.price
    specialP + normalP
  }).getOrElse(item.price * count)
}
