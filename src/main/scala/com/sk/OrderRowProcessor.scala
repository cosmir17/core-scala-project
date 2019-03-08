package com.sk

import com.sk.OrderEventFileParser.OrderRow
import com.sk.OrderRowProcessor.OrderRowsWithIdx
import com.sk.enums.Instruction.{Delete, New, Update}

object OrderRowProcessor {

  type OrderRowsWithIdx = Seq[(OrderRow, Int)]

  /**
    * this returns a processed(market update) OrderRow seq from raw OrderRows from Order event files.
    * @param orderRows
    * @return OrderRows
    */
  def compute(orderRows: Seq[OrderRow]) : Seq[OrderRow] = new OrderRowProcessor(orderRows).processedSeq
}

class OrderRowProcessor(orderRows: Seq[OrderRow]) {
  val processedSeq: Seq[OrderRow] = {
    orderRows.foldLeft[OrderRowsWithIdx](Seq())((foldSeqIdxd, origRow) => {
      val marketUpdateRows = foldSeqIdxd.filter(_._1.side == origRow.side).filterNot(_._1.instruction == Delete)
      val marketN = marketUpdateRows.filter(_._1.priceLevelIndex.exists(existingPriceIdx => origRow.priceLevelIndex.exists(_ <= existingPriceIdx)))
      val marketU = marketUpdateRows.filter(_._1.priceLevelIndex.exists(origRow.priceLevelIndex.contains))
      val marketNU = (marketN ++ marketU).distinct.sortBy(_._2)
      val foldSeq = foldSeqIdxd.map(_._1)

      val resultSeq: Seq[OrderRow] = (origRow.instruction, marketN, marketU) match {
        case (New, Seq(), Seq()) =>
          origRow +: foldSeq
        case (New, _, _) =>
          val (front, back) = foldSeq.splitAt(marketN.last._2 + 1)
          front ++ (origRow +: back)
        case (Update, _, _) =>
          val (front, back) = foldSeq.splitAt(marketU.filterNot(_._1.instruction == Update).head._2)
          front ++ (origRow +: back.slice(1, back.size))
        case (Delete, _, _) =>
          val (front, back) = foldSeq.splitAt(marketNU.last._2)
          front ++ back.slice(1, back.size)
      }
      resultSeq.zipWithIndex
    }).map(_._1)
  }
}