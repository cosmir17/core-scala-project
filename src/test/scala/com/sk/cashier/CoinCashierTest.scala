package com.sk.cashier

import org.scalatest.matchers.should._
import org.scalatest.funsuite.AnyFunSuite

class CoinCashierTest extends AnyFunSuite with Matchers {

  test("empty string should be given for 0 pence") {
    val result = CoinCashier.giveChange(0)
    result shouldBe ""
  }

  test("123 pence should be dispensed with every coin") {
    val result = CoinCashier.giveChange(123)
    result shouldBe "1 x £1, 1 x 20p, 1 x 2p, 1 x 1p"
  }

  test("100 pence should be dispensed with a one pound coin") {
    val result = CoinCashier.giveChange(100)
    result shouldBe "1 x £1"
  }

  test("200 pence should be dispensed with a two pound coin") {
    val result = CoinCashier.giveChange(200)
    result shouldBe "1 x £2"
  }

  test("1 pence should be dispensed with one pence coin") {
    val result = CoinCashier.giveChange(1)
    result shouldBe "1 x 1p"
  }

  test("222 pence should be dispensed with a two pound, a 20pence and 2pence coins") {
    val result = CoinCashier.giveChange(222)
    result shouldBe "1 x £2, 1 x 20p, 1 x 2p"
  }
}