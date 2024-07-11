package com.sk.codewars

object HumanReadableDurationFormatter:

  object nonzero:
    def unapply(n: Int): Boolean = n != 0

  enum TimeUnit(val unit: String, val duration: Int, val modulo: Int):
    case Year extends TimeUnit("year", 31536000, 100000)
    case Day extends TimeUnit("day", 86400, 365)
    case Hour extends TimeUnit("hour", 3600, 24)
    case Minute extends TimeUnit("minute", 60, 60)
    case Second extends TimeUnit("second", 1, 60)

  object TimeUnit:
    def unapply(timeUnit: TimeUnit): Option[(String, Int, Int)] = Some((timeUnit.unit, timeUnit.duration, timeUnit.modulo))

  def formatDuration(seconds: Int): String =
    TimeUnit.values.toList
      .map { case TimeUnit(unit, duration, modulo) => (seconds / duration % modulo, unit) }
      .collect {
        case (1, unit) => s"1 $unit"
        case (duration@ nonzero(), unit) => s"$duration ${unit}s"
      }
    match {
      case Nil => "now"
      case List(single) => single
      case list => s"${list.init.mkString(", ")} and ${list.last}"
    }
