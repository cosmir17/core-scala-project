package com.sk.binaryadder

import org.scalatest.matchers.should._
import org.scalatest.funsuite.AnyFunSuite

class BinaryAdderTest extends AnyFunSuite with Matchers:

  test("0 should be 1"):
    val result = BinaryAdder.addOne(Seq(0))
    result shouldBe Seq(1)

  test("10 should be 11"):
    val result = BinaryAdder.addOne(Seq(1,0))
    result shouldBe Seq(1,1)

  test("100 should be 101"):
    val result = BinaryAdder.addOne(Seq(1,0,0))
    result shouldBe Seq(1,0,1)

  test("1110 should be 1111"):
    val result = BinaryAdder.addOne(Seq(1,1,1,0))
    result shouldBe Seq(1,1,1,1)

  test("1010 should be 1011"):
    val result = BinaryAdder.addOne(Seq(1,0,1,0))
    result shouldBe Seq(1,0,1,1)

  test("111 should be 1000"):
    val result = BinaryAdder.addOne(Seq(1,1,1))
    result shouldBe Seq(1,0,0,0)

  test("101 should be 110"):
    val result = BinaryAdder.addOne(Seq(1,0,1))
    result shouldBe Seq(1,1,0)

  test("1110111 should be 1111000"):
    val result = BinaryAdder.addOne(Seq(1,1,1,0,1,1,1))
    result shouldBe Seq(1,1,1,1,0,0,0)

  test("0111 should be 1000"):
    val result = BinaryAdder.addOne(Seq(0,1,1,1))
    result shouldBe Seq(1,0,0,0)

  test("1001 should be 1010"):
    val result = BinaryAdder.addOne(Seq(1,0,0,1))
    result shouldBe Seq(1,0,1,0)

  test("0101 should be 110"):
    val result = BinaryAdder.addOne(Seq(0,1,0,1))
    result shouldBe Seq(1,1,0)

  test("000 should be 1"):
    val result = BinaryAdder.addOne(Seq(0,0,0))
    result shouldBe Seq(1)

  test("non-binary numbers are not allowed"):
    the [IllegalArgumentException] thrownBy {
      BinaryAdder.addOne(Seq(3, 0, 0))
    } should have message "Input sequence contains non-binary number(s)"
  
