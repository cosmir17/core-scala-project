package com.sk.codewars

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.*

class HumanReadableDurationFormatterTest extends AnyFlatSpec with Matchers {

  "HumanTime.formatDuration" should "pass 0 test" in {
    val result = HumanReadableDurationFormatter.formatDuration(0)
    result shouldBe "now"
  }

  "HumanTime.formatDuration" should "pass basic tests" in {
    val testCases = List( //seconds, expected
      (1, "1 second"),
      (62, "1 minute and 2 seconds"),
      (120, "2 minutes"),
      (3600, "1 hour"),
      (3662, "1 hour, 1 minute and 2 seconds"),
      (900000000, "28 years, 196 days and 16 hours")
    )

    testCases.foreach {
      case (seconds, expected) => assertResult(expected, s"\nInput:\n  seconds = $seconds") {
        HumanReadableDurationFormatter.formatDuration(seconds)
      }
    }
  }
}