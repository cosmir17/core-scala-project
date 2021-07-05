package com.sk.orderbook

import com.sk.orderbook.ConsoleInputParser.ConsoleInput
import org.scalatest.matchers.should._
import org.scalatest.funsuite.AnyFunSuite

class ConsoleInputParserTest extends AnyFunSuite with Matchers {

  test("should convert a string to ConsoleInput object") {
    val input = "prog updates.txt 10.0 3"
    val result = ConsoleInputParser.compute(input)
    result shouldBe ConsoleInput("updates.txt", 10d, 3)
  }

  test("should convert a string with zero tick value to ConsoleInput object") {
    val input = "prog updates.txt 0.0 3"
    val result = ConsoleInputParser.compute(input)
    result shouldBe ConsoleInput("updates.txt", 0d, 3)
  }

  test("should convert a string without prog with zero tick and depth values to ConsoleInput object") {
    val input = "prog updates.txt 0.0 0"
    val result = ConsoleInputParser.compute(input)
    result shouldBe ConsoleInput("updates.txt", 0d, 0)
  }

  test("should throw an exception without a command") {
    val input = "updates.txt 10.0 3"
    the [IllegalArgumentException] thrownBy {ConsoleInputParser.compute(input)}
  }

  test("should throw an exception when tick value is int") {
    val input = "updates.txt 10 3"
    the [IllegalArgumentException] thrownBy {ConsoleInputParser.compute(input)}
  }

  test("should throw an exception when file is not present") {
    val input = "prog 10.0 3"
    the [IllegalArgumentException] thrownBy {ConsoleInputParser.compute(input)}
  }

  test("should throw an exception when file name and tick are empty") {
    val input = "prog 3"
    the [IllegalArgumentException] thrownBy {ConsoleInputParser.compute(input)}
  }

  test("should throw an exception when it's empty") {
    val input = ""
    the [IllegalArgumentException] thrownBy {ConsoleInputParser.compute(input)}
  }

}


