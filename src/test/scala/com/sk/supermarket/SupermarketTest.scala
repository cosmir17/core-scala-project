package com.sk.supermarket

import com.sk.supermarket.Supermarket._
import org.scalatest.matchers.should._
import org.scalatest.funsuite.AnyFunSuite

class SupermarketTest extends AnyFunSuite with Matchers {

  test("Buying A, B, C & D") {
    val result = Supermarket.calculateTotalPrice(List(A, B, C, D))
    result shouldBe 115
  }

  test("Buying 3As, B, C & D") {
    val result = Supermarket.calculateTotalPrice(List(A, A, A, B, C, D))
    result shouldBe 195
  }

  test("Buying 3As, 2Bs, C & D") {
    val result = Supermarket.calculateTotalPrice(List(A, A, A, B, C, D, B))
    result shouldBe 210
  }

  test("Buying empty case") {
    val result = Supermarket.calculateTotalPrice(List())
    result shouldBe 0
  }

  test("Buying C & D") {
    val result = Supermarket.calculateTotalPrice(List(C, D))
    result shouldBe 35
  }

}