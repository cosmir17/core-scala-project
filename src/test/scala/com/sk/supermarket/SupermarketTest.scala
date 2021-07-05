package com.sk.supermarket

import com.sk.supermarket.Supermarket._
import org.scalatest.matchers.should._
import org.scalatest.funsuite.AnyFunSuite

class SupermarketTest extends AnyFunSuite with Matchers {

  test("A B C D") {
    val result = Supermarket.calculateTotalPrice(List(A, B, C, D))
    result shouldBe 115
  }

  test("3A B C D") {
    val result = Supermarket.calculateTotalPrice(List(A, A, A, B, C, D))
    result shouldBe 195
  }

  test("3A 2B C D") {
    val result = Supermarket.calculateTotalPrice(List(A, A, A, B, C, D, B))
    result shouldBe 210
  }

  test("empty case") {
    val result = Supermarket.calculateTotalPrice(List())
    result shouldBe 0
  }

  test("C D") {
    val result = Supermarket.calculateTotalPrice(List(C, D))
    result shouldBe 35
  }

}