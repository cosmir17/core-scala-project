package com.sk

import com.sk.enums.Instruction.Instruction
import com.sk.enums.{Instruction, Side}
import com.sk.enums.Side.Side

import scala.io.Source
import scala.util.{Failure, Success, Try}

object OrderEventFileParser {

  case class OrderRow(instruction: Instruction, side: Side, priceLevelIndex: Option[Int], price: Option[Int], quantity: Option[Int])

  val eventR = """(\D\s\D\s\d+\s\d+\s\d+)""".r
  val deleteEventR = """D(\s\D\s\d+)""".r

  /**
    * read an order event file specified in user command and returns recognisable data object
    * @param fileName e.g. updates2.txt
    * @return Sequence of OrderRow s
    */
  def compute(fileName: String): Seq[OrderRow] = fileNameToStrRows(fileName)
    .foldLeft[Seq[OrderRow]](Seq())((_, fromOption) => convertStrRowsToOrderRows(fromOption))

  /**
    * this method is public as used in tests for this class' main feature
    * @param strRows
    * @return
    */
  def convertStrRowsToOrderRows(strRows: Seq[String]): Seq[OrderRow] = strRows.filter(_.nonEmpty).map(parseStringToOrderRow)

  private def fileNameToStrRows(fileName: String) : Option[Seq[String]] =
    Try(Source.fromFile(fileName).getLines.toSeq) orElse Try(Source.fromResource(fileName).getLines.toSeq) match {
      case Success(rows) => Some(rows)
      case Failure(_: NullPointerException) => throw new IllegalArgumentException(s"File with $fileName is not present")
      case Failure(_) => throw new IllegalArgumentException(s"Unknown file error")
    }

  private def parseStringToOrderRow(str: String): OrderRow =
    (eventR.findFirstIn(str) orElse deleteEventR.findFirstIn(str)).map(_.split(" ").toList) match {
    case Some(i::s::priceLevelIndex::price::quantity::Nil) if i.toLowerCase() != "d" =>
      OrderRow(Instruction(i), Side(s), Some(priceLevelIndex.toInt), Some(price.toInt), Some(quantity.toInt))
    case Some(i::s::priceLevelIndex::Nil) =>
      OrderRow(Instruction(i), Side(s), Some(priceLevelIndex.toInt), None, None)
    case _ =>
      throw new IllegalArgumentException("Order event file is not in the correct format")
  }
}
