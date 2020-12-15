package models.web.rest

import models.web.rest.LevelData.Rounds
import service.leagueinfo.LoadingInfo

trait LevelData {
  def seasonOffset: Int
  def seasonRoundInfo: Seq[(Int, Rounds)]
  def currency: String
  def currencyRate: Double
  def countries: Seq[(Int, String)]
}

trait CountryLevelData extends LevelData {
  def loadingInfo: LoadingInfo
}

object LevelData {
  type Rounds = Seq[Int]
}