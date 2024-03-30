package com.sk.orderbook.enums

object Instruction:
  enum InstructionEnum:
    case Update
    case Delete
    case New
  
  import InstructionEnum.*
  def apply(code: String): InstructionEnum =
    if ("U".toLowerCase() == code.toLowerCase()) Update
    else if ("D".toLowerCase() == code.toLowerCase()) Delete
    else if ("N".toLowerCase() == code.toLowerCase()) New
    else throw new IllegalArgumentException(s"$code is not a recognised Instruction code")
