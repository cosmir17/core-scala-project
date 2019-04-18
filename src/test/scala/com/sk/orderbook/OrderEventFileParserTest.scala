package com.sk.orderbook

import com.sk.orderbook.OrderEventFileParser._
import com.sk.orderbook.enums.Instruction.{Delete, New, Update}
import com.sk.orderbook.enums.Side.{Ask, Bid}
import org.scalatest.{FunSuite, Matchers}

class OrderEventFileParserTest extends FunSuite with Matchers {

  test("should parse normal order event history to list of RawOrderRows") {
    val history = Seq(
      "N B 1 5 30",
      "N B 2 4 40",
      "N A 1 6 10",
      "N A 2 7 10",
      "U A 2 7 20",
      "U B 1 5 40"
    )
    val result = OrderEventFileParser.convertStrRowsToOrderRows(history)

    result shouldBe Seq(
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Ask, Some(2), Some(7), Some(10)),
      OrderRow(Update, Ask, Some(2), Some(7), Some(20)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )
  }

  test("should parse delete order event history without throwing an exception") {
    val history = Seq(
      "N B 1 5 30",
      "D A 1",
      "U B 1 5 40"
    )

    val result = OrderEventFileParser.convertStrRowsToOrderRows(history)

    result shouldBe Seq(
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(Delete, Ask, Some(1), None, None),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )
  }

  test("should skip empty lines") {
    val history = Seq("N B 1 5 30", "", "U B 1 5 40")
    val result = OrderEventFileParser.convertStrRowsToOrderRows(history)

    result shouldBe Seq(
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )
  }

  test("should return an empty seq if input is empty") {
    val history = Seq()
    val result = OrderEventFileParser.convertStrRowsToOrderRows(history)
    result shouldBe Seq()
  }

  test("should not throw an exception when it has an extra space") {
    val history = Seq(
      "N B 1 5 30",
      "N B 2 4 40 ",
      "N A 1 6 10   "
    )
    val result = OrderEventFileParser.convertStrRowsToOrderRows(history)

    result shouldBe Seq(
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Ask, Some(1), Some(6), Some(10))
    )
  }

  test("should not throw an exception for delete order when it has an extra space") {
    val history = Seq("D B 1 ")
    val result = OrderEventFileParser.convertStrRowsToOrderRows(history)
    result shouldBe Seq(OrderRow(Delete, Bid, Some(1), None, None))
  }

  test("should throw an exception when it's missing price and quantity values") {
    val history = Seq(
      "N B 1 5 30",
      "N B 2 40",
      "N A 1 6 10",
      "N A 2 7 10",
      "U A 2 7",
      "U B 1 5 40"
    )

    the [IllegalArgumentException] thrownBy {OrderEventFileParser.convertStrRowsToOrderRows(history)}
  }

  test("should throw an exception when it's missing instruction and side values") {
    val history = Seq(
      "N B 1 5 30",
      "N B 2 4 40",
      "A 1 6 10",
      "N 2 7 10",
      "U A 2 7 20",
      "U B 1 5 40"
    )

    the [IllegalArgumentException] thrownBy {OrderEventFileParser.convertStrRowsToOrderRows(history)}
  }

  test("should throw an exception when it has not a proper instruction value") {
    val history = Seq("P A 1 6 10")
    the [IllegalArgumentException] thrownBy {OrderEventFileParser.convertStrRowsToOrderRows(history)}
  }

  test("should throw an exception when it's delete and it has price and quantity values") {
    val history = Seq("D A 1 6 10")
    the [IllegalArgumentException] thrownBy {OrderEventFileParser.convertStrRowsToOrderRows(history)}
  }

  test("should throw an exception when it has not a proper side value") {
    val history = Seq("D E 1 6 10")
    the [IllegalArgumentException] thrownBy {OrderEventFileParser.convertStrRowsToOrderRows(history)}
  }
}
