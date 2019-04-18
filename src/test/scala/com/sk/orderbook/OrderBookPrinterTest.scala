package com.sk.orderbook

import com.sk.orderbook.OrderBookProducer.OrderBookRow
import org.scalatest.{FunSuite, Matchers}

class OrderBookPrinterTest extends FunSuite with Matchers {

  test("should compute a list of order book strings when there are None values") {
    val orderBook = Seq(
      OrderBookRow(Some(50.0), Some(40), Some(80.0), Some(25)),
      OrderBookRow(Some(40.0), Some(40), Some(70.0), Some(20)),
      OrderBookRow(None, None, Some(60.0), Some(10))
    )
    val strings = OrderBookPrinter.compute(orderBook)

    strings shouldBe Seq(
      "Output :",
      "50.0,40,80.0,25",
      "40.0,40,70.0,20",
      "0.0,0,60.0,10"
    )
  }

  test("should compute a list of order book strings without any None values") {
    val orderBook = Seq(
      OrderBookRow(Some(60.0), Some(100), Some(60.0), Some(100)),
      OrderBookRow(Some(60.0), Some(80), Some(60.0), Some(100)),
      OrderBookRow(Some(60.0), Some(70), Some(60.0), Some(100))
    )
    val strings = OrderBookPrinter.compute(orderBook)

    strings shouldBe Seq(
      "Output :",
      "60.0,100,60.0,100",
      "60.0,80,60.0,100",
      "60.0,70,60.0,100"
    )
  }

  test("should compute an empty list when there is order book") {
    val orderBook = Seq()
    val strings = OrderBookPrinter.compute(orderBook)
    strings shouldBe Seq("Output is empty")
  }

}
