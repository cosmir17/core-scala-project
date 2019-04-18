package com.sk.orderbook

import com.sk.orderbook.OrderEventFileParser.OrderRow
import com.sk.orderbook.enums.Instruction.{Delete, New, Update}
import com.sk.orderbook.enums.Side.{Ask, Bid}
import org.scalatest.{FunSuite, Matchers}

class OrderRowProcessorTest extends FunSuite with Matchers {

  test("should do new and update shifting market update process") {
    val inputRows = Seq(
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Ask, Some(2), Some(7), Some(10)),
      OrderRow(Update, Ask, Some(2), Some(7), Some(20)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )

    val result = OrderRowProcessor.compute(inputRows)
    result shouldBe Seq(
      OrderRow(Update, Ask, Some(2), Some(7), Some(20)),
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )
  }

  test("should do new and update shifting market update process, a different example") {
    val inputRows = Seq(
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Ask, Some(1), Some(7), Some(20)),
      OrderRow(New, Ask, Some(1), Some(8), Some(25)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )

    val result = OrderRowProcessor.compute(inputRows)
    result shouldBe Seq(
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Ask, Some(1), Some(7), Some(20)),
      OrderRow(New, Ask, Some(1), Some(8), Some(25)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )
  }

  test("should do delete, new and update shifting market update process") {
    val inputRows = Seq(
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Ask, Some(1), Some(7), Some(20)),
      OrderRow(New, Ask, Some(1), Some(8), Some(25)),
      OrderRow(Delete, Ask, Some(1), None, None),
      OrderRow(Delete, Ask, Some(1), None, None),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )

    val result = OrderRowProcessor.compute(inputRows)
    result shouldBe Seq(
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )
  }

  test("should do delete shifting market update process") {
    val inputRows = Seq(
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Ask, Some(1), Some(7), Some(20)),
      OrderRow(New, Ask, Some(1), Some(8), Some(25)),
      OrderRow(Delete, Ask, Some(1), None, None),
      OrderRow(Delete, Ask, Some(1), None, None),
      OrderRow(Delete, Ask, Some(1), None, None)
    )

    val result = OrderRowProcessor.compute(inputRows)
    result shouldBe Seq()
  }

  test("should do new shifting market update process") {
    val inputRows = Seq(
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Bid, Some(1), Some(6), Some(70)),
      OrderRow(New, Bid, Some(1), Some(6), Some(80)),
      OrderRow(New, Bid, Some(1), Some(6), Some(100))
    )

    val result = OrderRowProcessor.compute(inputRows)
    result shouldBe Seq(
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(1), Some(6), Some(70)),
      OrderRow(New, Bid, Some(1), Some(6), Some(80)),
      OrderRow(New, Bid, Some(1), Some(6), Some(100))
    )
  }

  test("should do repeatative update shifting market update process") {
    val inputRows = Seq(
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Bid, Some(1), Some(6), Some(70)),
      OrderRow(Update, Bid, Some(1), Some(6), Some(80)),
      OrderRow(Update, Bid, Some(1), Some(6), Some(100))
    )

    val result = OrderRowProcessor.compute(inputRows)
    result shouldBe Seq(
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(Update, Bid, Some(1), Some(6), Some(80)),
      OrderRow(Update, Bid, Some(1), Some(6), Some(100))
    )
  }

  test("should return an empty seq if input is empty") {
    val inputRows = Seq()
    val result = OrderRowProcessor.compute(inputRows)
    result shouldBe Seq()
  }
}
