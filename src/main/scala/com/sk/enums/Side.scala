package com.sk.enums

object Side {
  sealed trait Side
  case object Bid extends Side
  case object Ask extends Side

  def apply (code: String) =
    if ("B".toLowerCase() == code.toLowerCase()) Bid
    else if ("A".toLowerCase() == code.toLowerCase()) Ask
    else throw new IllegalArgumentException(s"$code is not a recognised Side code")
}
