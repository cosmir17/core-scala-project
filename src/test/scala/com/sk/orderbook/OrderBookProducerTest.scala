package com.sk.orderbook

import com.sk.orderbook.ConsoleInputParser.ConsoleInput
import com.sk.orderbook.OrderBookProducer.OrderBookRow
import com.sk.orderbook.OrderEventFileParser.OrderRow
import com.sk.orderbook.enums.Instruction.{New, Update}
import com.sk.orderbook.enums.Side.{Ask, Bid}
import org.scalatest.{FunSuite, Matchers}

class OrderBookProducerTest extends FunSuite with Matchers {

  test("should process orders new and update orders") {
    val consoleInput = ConsoleInput("", 10.0, 2)
    val inputRows = Seq(
      OrderRow(Update, Ask, Some(2), Some(7), Some(20)),
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )

    val result = OrderBookProducer.produce(consoleInput, inputRows)
    result shouldBe Seq(
      OrderBookRow(Some(50.0), Some(40), Some(60.0), Some(10)),
      OrderBookRow(Some(40.0), Some(40), Some(70.0), Some(20))
    )
  }

  test("should process new and update shifting market update process, a different example") {
    val consoleInput = ConsoleInput("", 10.0, 3)
    val inputRows = Seq(
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Ask, Some(1), Some(7), Some(20)),
      OrderRow(New, Ask, Some(1), Some(8), Some(25)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )

    val result = OrderBookProducer.produce(consoleInput, inputRows)
    result shouldBe Seq(
      OrderBookRow(Some(50.0), Some(40), Some(80.0), Some(25)),
      OrderBookRow(Some(40.0), Some(40), Some(70.0), Some(20)),
      OrderBookRow(None, None, Some(60.0), Some(10))
    )
  }

  test("should do delete, new and update shifting market update process") {
    val consoleInput = ConsoleInput("", 10.0, 3)
    val inputRows = Seq(
      OrderRow(New, Ask, Some(1), Some(6), Some(10)),
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(Update, Bid, Some(1), Some(5), Some(40))
    )

    val result = OrderBookProducer.produce(consoleInput, inputRows)
    result shouldBe Seq(
      OrderBookRow(Some(50.0), Some(40), Some(60.0), Some(10)),
      OrderBookRow(Some(40.0), Some(40), None, None),
      OrderBookRow(None, None, None, None)
    )
  }

  test("should compute order books when input rows without any update and delete instructions are given") {
    val consoleInput = ConsoleInput("", 10.0, 5)
    val inputRows = Seq(
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(1), Some(6), Some(70)),
      OrderRow(New, Bid, Some(1), Some(6), Some(80)),
      OrderRow(New, Bid, Some(1), Some(6), Some(100))
    )

    val result = OrderBookProducer.produce(consoleInput, inputRows)
    result shouldBe Seq(
      OrderBookRow(Some(60.0), Some(100), None, None),
      OrderBookRow(Some(60.0), Some(80), None, None),
      OrderBookRow(Some(60.0), Some(70), None, None),
      OrderBookRow(Some(50.0), Some(30), None, None),
      OrderBookRow(Some(40.0), Some(40), None, None),
    )
  }

  test("should compute Empty order books if console input requires a depth when there is no input row") {
    val consoleInput = ConsoleInput("", 10.0, 2)
    val inputRows = Seq()
    val result = OrderBookProducer.produce(consoleInput, inputRows)
    result shouldBe Seq(OrderBookRow(None, None, None, None), OrderBookRow(None, None, None, None))
  }

  test("should compute no order books if console input requires 0 depth") {
    val consoleInput = ConsoleInput("", 10.0, 0)
    val inputRows = Seq(
      OrderRow(New, Bid, Some(2), Some(4), Some(40)),
      OrderRow(New, Bid, Some(1), Some(5), Some(30)),
      OrderRow(New, Bid, Some(1), Some(6), Some(70)),
      OrderRow(New, Bid, Some(1), Some(6), Some(80)),
      OrderRow(New, Bid, Some(1), Some(6), Some(100))
    )

    val result = OrderBookProducer.produce(consoleInput, inputRows)
    result shouldBe Seq()
  }

  test("should compute order books when it has only one update order") {
    val consoleInput = ConsoleInput("", 10.0, 1)
    val inputRows = Seq(
      OrderRow(Update, Bid, Some(1), Some(6), Some(100))
    )

    val result = OrderBookProducer.produce(consoleInput, inputRows)
    result shouldBe Seq(
      OrderBookRow(Some(60.0), Some(100), None, None)
    )
  }

  test("should return an empty seq if input is empty") {
    val consoleInput = ConsoleInput("", 10.0, 0)
    val inputRows = Seq()
    val result = OrderBookProducer.produce(consoleInput, inputRows)
    result shouldBe Seq()
  }
}
