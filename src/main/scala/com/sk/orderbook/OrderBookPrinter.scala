package com.sk.orderbook

import com.sk.orderbook.OrderBookProducer.OrderBookRow

object OrderBookPrinter:
  private val emptyOutputMessage = "Output is empty"
  private val headline = "Output :"

  def compute(orderBookRows: Seq[OrderBookRow]) : Seq[String] =
    orderBookRows match
      case Seq() => Seq(emptyOutputMessage)
      case _ => Seq(headline) ++ orderBookRows.map(o => s"${o.bidPrice.dToStr()},${o.bidQuantity.iToStr()},${o.askPrice.dToStr()},${o.askQuantity.iToStr()}")

  extension [T](inputData: Option[T])
    def dToStr() = inputData match
      case None => "0.0"
      case Some(n) => n.toString

    def iToStr() = inputData match
      case None => "0"
      case Some(n) => n.toString
