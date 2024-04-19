package com.sk.supermarket

object Supermarket:
  case class Promotion(count: Int, newPrice: Int)

  enum Item(val price: Int, val promotion: Option[Promotion] = None):
    case A extends Item(50, Some(Promotion(3, 130)))
    case B extends Item(30, Some(Promotion(2, 45)))
    case C extends Item(20)
    case D extends Item(15)

  /**
    * calculate the total price of a list of items
    * @param items
    * @return price
    */
  def calculateTotalPrice(items: List[Item]): Int = items
      .groupBy(identity)
      .map { case (item, listOfItems) => (item.price, listOfItems.size, item.promotion) }
      .map {
        case (price, count, Some(promotion)) => applyPromotionForAGroupOfItems(price, count, promotion)
        case (price, count, None)            => price * count
         }
      .sum

  private def applyPromotionForAGroupOfItems(price: Int, count: Int, promotion: Promotion) =
    (count / promotion.count * promotion.newPrice) + (count % promotion.count * price)
