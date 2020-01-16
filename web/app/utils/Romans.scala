package utils

import com.google.common.collect.ImmutableBiMap

object Romans {
  private val map = ImmutableBiMap.builder()
    .put("I", 1)
    .put("II", 2)
    .put("III", 3)
    .put("IV", 4)
    .put("V", 5)
    .put("VI", 6)
    .put("VII", 7)
    .put("VIII", 8)
    .put("IX", 9)
    .put("X", 10)
    .put("XI", 11)
    .put("XII", 12)
    .build()

  def apply(roman: String) = map.get(roman)

  def apply(arabic: Int) = map.inverse().get(arabic)
}
