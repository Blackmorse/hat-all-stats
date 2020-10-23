package models.web.rest

import models.web.rest.LevelData.Rounds

trait LevelData {
  def seasonRoundInfo: Seq[(Int, Rounds)]
  def currency: String
  def currencyRate: Double
}

object LevelData {
  type Rounds = Seq[Int]
}