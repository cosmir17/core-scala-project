package com.sk.transaction

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class BatchFilteringTest extends AnyFreeSpec with Matchers:

  "Transaction2 should be" - {
    "successful" in:
      val albert = Transaction("albert-id", 2000, "ETH", "account-y", "account-p")
      val robert = Transaction("robert-id", 2000, "ETH", "account-e", "account-v")
      Filtering.filter(Seq(albert, robert, albert)) shouldBe
        Seq(Result(true), Result(true), Result(true))

    "unsuccessful" in:
      val albert = Transaction("albert-id", 2000, "ETH", "account-y", "account-p")
      val robert = Transaction("robert-id", 2000, "GBP", "account-e", "account-v")
      val james = Transaction("james-id", 2000, "GBP", "account-t", "account-m")
      Filtering.filter(Seq(albert, robert, james)) shouldBe
        Seq(Result(true), Result(false, Some("INVALID_CURRENCY")), Result(false, Some("INVALID_CURRENCY")))

    "unsuccessful for guilty by association2" in:
      val transactionNormal = Transaction("peter-id", 2000, "ETH", "account-a", "account-b")
      val transactionBad = Transaction("peter-id", 2000, "ETH", "account-c", "account-a")
      Filtering.filter(Seq(transactionNormal, transactionBad, transactionNormal)) shouldBe
        Seq(Result(true), Result(false, Some("LAUNDER")), Result(false, Some("LAUNDER")))

    "unsuccessful for guilty by association3" in:
      val transactionNormal = Transaction("peter-id", 2000, "ETH", "account-a", "account-b")
      val transactionBad = Transaction("peter-id", 2000, "ETH", "account-c", "account-a")
      val transactionNormalSecond = Transaction("peter-id", 2000, "ETH", "account-b", "account-d")
      Filtering.filter(Seq(transactionNormal, transactionBad, transactionNormal, transactionNormalSecond)) shouldBe
        Seq(Result(true), Result(false, Some("LAUNDER")), Result(false, Some("LAUNDER")), Result(false, Some("LAUNDER")))
  }
