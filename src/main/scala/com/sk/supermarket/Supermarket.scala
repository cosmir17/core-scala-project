package com.sk.supermarket

object Supermarket {
  case class Promotion(count: Int, newPrice: Int)

  sealed abstract class Item(val price: Int, val promotion: Option[Promotion] = None)
  case object A extends Item(50, Some(Promotion(3, 130)))
  case object B extends Item(30, Some(Promotion(2, 45)))
  case object C extends Item(20)
  case object D extends Item(15)

  /**
    * calculate the total price of a list of items
    * @param items
    * @return price
    */
  def calculateTotalPrice(items: List[Item]): Int = items
      .groupBy(identity)
      .map { case (item, listOfItems) => (item.price, listOfItems.size, item.promotion) }
      .map {
        case (price, count, Some(promotion)) => applyPromotion(price, count, promotion)
        case (price, count, None) => price * count
         }
      .sum

  private def applyPromotion(price: Int, count: Int, promotion: Promotion) =
    (count / promotion.count * promotion.newPrice) + (count % promotion.count * price)
}
