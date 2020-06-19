package models.web

import com.blackmorse.hattrick.api.worlddetails.model.League
import service.LeagueInfo

abstract class AbstractWebDetails {
  val leagueInfo: LeagueInfo
}
