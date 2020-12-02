package models.web.rest

import java.util.Date

import models.web.rest.LevelData.Rounds
import service.leagueinfo.LoadingInfo

trait LevelData {
  def seasonOffset: Int
  def seasonRoundInfo: Seq[(Int, Rounds)]
  def currency: String
  def currencyRate: Double
}

trait CountryLevelData extends LevelData {
  def loadingInfo: LoadingInfo
}

object LevelData {
  type Rounds = Seq[Int]
}