package com.blackmorse.hattid.web.utils

import hattid.CommonData._

object Romans {
  def apply(roman: String) = romansToArab(roman)

  def apply(arabic: Int) = arabToRomans(arabic)
}
