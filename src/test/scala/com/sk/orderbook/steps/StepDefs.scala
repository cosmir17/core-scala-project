package com.sk.orderbook.steps

import java.io.{ByteArrayOutputStream, PrintStream}
import com.sk.orderbook.MainClass
import io.cucumber.datatable.DataTable
import io.cucumber.scala.{EN, ScalaDsl}
import org.scalatest.matchers.should.*

import scala.jdk.CollectionConverters.*
import scala.compiletime.uninitialized

class StepDefs extends ScalaDsl with EN with Matchers:
  private val outContent = new ByteArrayOutputStream
  private val errContent = new ByteArrayOutputStream
  private var lines: Seq[String] = uninitialized

  Given("""^The following order events are made$"""){ (data: DataTable) =>
    val initialJavaMap = data.asMaps(classOf[String], classOf[String])
    val mapSeq = initialJavaMap.toArray().toSeq
    val castedMapSeq: Seq[Map[String, String]] = mapSeq.map { anyRef =>
      val map = anyRef.asInstanceOf[java.util.Map[String, String]]
      map.asScala.toMap
    }
    lines = castedMapSeq.map(map => {
          val instruction = map.get("instruction")
          val side = map.get("side")
          val priceLevelIndex = map.get("price_level_index")
          val price = map.get("price")
          val quantity = map.get("quantity")
          s"$instruction $side $priceLevelIndex $price $quantity"
        })
  }

  When("""the main app runs with tick size {double} and book depth {int}"""){ (tickSize: Double, depth: Int) =>
    new MainClass(s"prog updates.txt $tickSize $depth").orderBookMsgs.foreach(println)
  }

  Then("""^the following should be printed$"""){ (data: DataTable) =>
    case class Result(bidPrice: Option[String], bidQuantity: Option[String], askPrice: Option[String], askQuantity: Option[String])

    val mapSeq = data
      .asMaps(classOf[String], classOf[String])
      .toArray().toSeq
    val castedMapSeq: Seq[Map[String, String]] = mapSeq.map { anyRef =>
      val map = anyRef.asInstanceOf[java.util.Map[String, String]]
      map.asScala.toMap
    }
    val tableRows = castedMapSeq.map(map => {
        val bidPrice = map.get("bid_price")
        val bidQuantity = map.get("bid_quantity")
        val askPrice = map.get("ask_price")
        val askQuantity = map.get("ask_quantity")
        Result(bidPrice, bidQuantity, askPrice, askQuantity)
      })

    System.setOut(new PrintStream(outContent))
    System.setErr(new PrintStream(errContent))

    val msgLines = tableRows.map(row => s"${row.bidPrice.get}, ${row.bidQuantity.get}, ${row.askPrice.get}, ${row.askQuantity.get}")
    msgLines.foreach(outContent.toString should include (_))
  }

  Then("""^'(.*)' should be printed.$"""){ (message: String) =>
    System.setOut(new PrintStream(outContent))
    System.setErr(new PrintStream(errContent))
    outContent.toString should include (message)
  }