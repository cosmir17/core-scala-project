package com.sk.cashier

object CoinCashier {
  case class Coin(amount: Int, printed: String)
  case class Change(remainder: Int, divisor: Option[Int], coin: Option[Coin])

  private lazy val ukCoins = Seq(Coin(200, "£2"), Coin(100, "£1"),
    Coin(50, "50p"), Coin(20, "20p"), Coin(10, "10p"),
    Coin(5, "5p"), Coin(2, "2p"), Coin(1, "1p"))

  def giveChange(pence: Int) : String = ukCoins
    .scanLeft(Change(pence, None, None))((change, coin) => //this seed disappears at the filter line
      Change(change.remainder % coin.amount, change.remainder.divideBy(coin.amount), Some(coin)))
    .filter(_.divisor.nonEmpty)
    .map(change => s"${change.divisor.get} x ${change.coin.get.printed}")
    .mkString(", ")

  implicit class DivisorOptioniser(remainder : Int) {
    def divideBy(amount: Int) : Option[Int] = Some(remainder / amount).filter(_ != 0)
  }
}