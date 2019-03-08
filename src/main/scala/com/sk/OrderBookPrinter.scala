package com.sk

import com.sk.OrderBookProducer.OrderBookRow
import scala.language.implicitConversions

object OrderBookPrinter {
  private val emptyOutputMessage = "Output is empty"
  private val headline = "Output :"

  def compute(orderBookRows: Seq[OrderBookRow]) : Seq[String] =
    orderBookRows match {
      case Seq() => Seq(emptyOutputMessage)
      case _ => Seq(headline) ++ orderBookRows.map(o => s"${o.bidPrice.dToStr()},${o.bidQuantity.iToStr()},${o.askPrice.dToStr()},${o.askQuantity.iToStr()}")
    }

  implicit class OptionTypeConverterToString[_](inputData: Option[_]) {
    implicit def dToStr() = inputData match {
      case None => "0.0"
      case Some(n) => n.toString
    }

    implicit def iToStr() = inputData match {
      case None => "0"
      case Some(n) => n.toString
    }
  }
}
