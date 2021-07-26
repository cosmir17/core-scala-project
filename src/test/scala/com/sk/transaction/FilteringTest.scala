package com.sk.transaction

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class FilteringTest extends AnyFreeSpec with Matchers {

  "Transaction should be" - {
    "successful" in {
      val transaction = Transaction("peter-id", 2000, "ETH", "peter-account", "lawson-account")
      Filtering.filter(transaction) shouldBe Result(true)
    }

    "unsuccessful for non ETH currency, GBP" in {
      val transaction = Transaction("peter-id", 2000, "GBP", "peter-account", "lawson-regex")
      Filtering.filter(transaction) shouldBe Result(false, Some("INVALID_CURRENCY"))
    }

    "unsuccessful for non ETH currency, USD" in {
      val transaction = Transaction("peter-id", 2000, "USD", "peter-account", "lawson-regex")
      Filtering.filter(transaction) shouldBe Result(false, Some("INVALID_CURRENCY"))
    }

    "unsuccessful for invalid currency" in {
      val transaction = Transaction("peter-id", 2000, "QWE", "peter-account", "lawson-regex")
      Filtering.filter(transaction) shouldBe Result(false, Some("INVALID_CURRENCY"))
    }

    "unsuccessful for invalid amount" in {
      val transaction = Transaction("peter-id", 300, "ETH", "peter-account", "lawson-regex")
      Filtering.filter(transaction) shouldBe Result(false, Some("INVALID_AMOUNT"))
    }

    "unsuccessful for invalid currency but amount is also invalid" in {
      val transaction = Transaction("peter-id", 300, "GBP", "peter-account", "lawson-regex")
      Filtering.filter(transaction) shouldBe Result(false, Some("INVALID_CURRENCY"))
    }

    "unsuccessful for empty user ID" in {
      val transaction = Transaction("", 2000, "ETH", "peter-account", "lawson-regex")
      Filtering.filter(transaction) shouldBe Result(false, Some("INVALID_USER_ID"))
    }

    "unsuccessful for empty AccountTo value" in {
      val transaction = Transaction("peter-id", 2000, "ETH", "account-regex", "")
      Filtering.filter(transaction) shouldBe Result(false, Some("INVALID_ACCOUNT_TO"))
    }

    "unsuccessful for launder from-account c" in {
      val transaction = Transaction("peter-id", 2000, "ETH", "account-c", "normal-account")
      Filtering.filter(transaction) shouldBe Result(false, Some("LAUNDER"))
    }

    "unsuccessful for launder to-account c" in {
      val transaction = Transaction("peter-id", 2000, "ETH", "normal-account", "account-c")
      Filtering.filter(transaction) shouldBe Result(false, Some("LAUNDER"))
    }

  }

}
