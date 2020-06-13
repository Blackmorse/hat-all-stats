package utils

import com.blackmorse.hattrick.common.CommonData._

object Romans {
  def apply(roman: String) = romansToArab.get(roman)

  def apply(arabic: Int) = arabToRomans.get(arabic)
}
