package com.blackmorse.hattid.web.models.web.rest

import com.blackmorse.hattid.web.service.leagueinfo.LoadingInfo
import com.blackmorse.hattid.web.models.web.rest.LevelData.Rounds

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