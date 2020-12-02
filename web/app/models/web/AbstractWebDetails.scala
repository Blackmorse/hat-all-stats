package models.web

import service.leagueinfo.LeagueInfo

abstract class AbstractWebDetails {
  val leagueInfo: LeagueInfo
  val currentRound: Int
}
