package com.sk

import com.sk.ConsoleInputParser.ConsoleInput
import com.sk.OrderEventFileParser.OrderRow
import com.sk.enums.Side.{Ask, Bid}

object OrderBookProducer {

  /**
    * None values are going to be translated to 0.0(price) or 0(quantity).
    * @param bidPrice
    * @param bidQuantity
    * @param askPrice
    * @param askQuantity
    */
  case class OrderBookRow(bidPrice: Option[Double], bidQuantity: Option[Int], askPrice: Option[Double], askQuantity: Option[Int])

  /**
    * @param consoleInput using ticks and depth
    * @param processedOrderRows
    * @return final logical product of order book processing
    */
  def produce(consoleInput: ConsoleInput, processedOrderRows: Seq[OrderRow]): Seq[OrderBookRow] = {
    val bids = processedOrderRows.reverse.filter(_.side == Bid)
    val asks = processedOrderRows.reverse.filter(_.side == Ask)

    val books = (bids.size == asks.size, bids.size < asks.size) match {
      case (true, _) =>
        for { (bid, ask) <- bids zip asks} yield OrderBookRow(bid.price.map(_.toDouble), bid.quantity, ask.price.map(_.toDouble), ask.quantity)
      case (_, true) =>
        val uptoBidsSize = bids.indices.map(i => OrderBookRow(bids(i).price.map(_.toDouble), bids(i).quantity, asks(i).price.map(_.toDouble), asks(i).quantity))
        (bids.size until asks.size).foldLeft[Seq[OrderBookRow]](uptoBidsSize)((foldedBooks, i) => foldedBooks :+ OrderBookRow(None, None, asks(i).price.map(_.toDouble), asks(i).quantity))
      case (false, false) =>
        val uptoAsksSize = asks.indices.map(i => OrderBookRow(bids(i).price.map(_.toDouble), bids(i).quantity, asks(i).price.map(_.toDouble), asks(i).quantity))
        (asks.size until bids.size).foldLeft[Seq[OrderBookRow]](uptoAsksSize)((foldedBooks, i) => foldedBooks :+ OrderBookRow(bids(i).price.map(_.toDouble), bids(i).quantity, None, None))
    }

    val newBook = books.map(o => o.copy(bidPrice = o.bidPrice.map(_ * consoleInput.tickSize), askPrice = o.askPrice.map(_ * consoleInput.tickSize)))

    (consoleInput.bookDepth == newBook.size, consoleInput.bookDepth > newBook.size) match {
      case (true, _) =>
        newBook
      case (_, true) =>
        (newBook.size until consoleInput.bookDepth).foldLeft[Seq[OrderBookRow]](newBook)((folded, _) => folded :+ OrderBookRow(None, None, None, None))
      case (false, false) =>
        val sizeDiff = newBook.size - consoleInput.bookDepth
        newBook.dropRight(sizeDiff)
    }
  }
}
