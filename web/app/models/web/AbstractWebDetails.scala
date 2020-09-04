package models.web

import service.LeagueInfo

abstract class AbstractWebDetails {
  val leagueInfo: LeagueInfo
  val currentRound: Int
}
