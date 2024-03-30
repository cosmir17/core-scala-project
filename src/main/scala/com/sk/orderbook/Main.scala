package com.sk.orderbook

import scala.util.{Failure, Success, Try}

val commandInputMsg = "******* Please enter your command, prog for normal command, exit to terminate *******"

object Main extends App:
  println(commandInputMsg)
  for (ln <- scala.io.Source.stdin.getLines) new MainClass(ln).orderBookMsgs.foreach(println)

class MainClass(input: String):
  val orderBookMsgs: Seq[String] =
    val logMsgSeq = Try { val consoleInput = ConsoleInputParser.compute(input)
    val orders = OrderEventFileParser.compute(consoleInput.fileName)
    val processedOrderRows = OrderRowProcessor.compute(orders)
    val orderBooks = OrderBookProducer.produce(consoleInput, processedOrderRows)
    OrderBookPrinter.compute(orderBooks) } match
      case Success(msgs) => msgs
      case Failure(e: IllegalArgumentException) => Seq(s"=> ${e.getMessage}")
      case Failure(e) => Seq(s"Unknown error: ${e.getMessage}")

    logMsgSeq :+ commandInputMsg