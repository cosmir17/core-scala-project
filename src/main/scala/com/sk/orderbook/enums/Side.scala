package com.sk.orderbook.enums

object Side:
  enum SideEnum:
    case Bid
    case Ask
  
  import SideEnum.*
  def apply(code: String): SideEnum =
    if ("B".toLowerCase() == code.toLowerCase()) Bid
    else if ("A".toLowerCase() == code.toLowerCase()) Ask
    else throw new IllegalArgumentException(s"$code is not a recognised Side code")
