package com.sk.orderbook.enums

object Instruction {
  sealed trait Instruction
  case object Update extends Instruction
  case object Delete extends Instruction
  case object New extends Instruction

  def apply (code: String) =
    if ("U".toLowerCase() == code.toLowerCase()) Update
    else if ("D".toLowerCase() == code.toLowerCase()) Delete
    else if ("N".toLowerCase() == code.toLowerCase()) New
    else throw new IllegalArgumentException(s"$code is not a recognised Instruction code")
}
