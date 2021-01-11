package models.clickhouse

import chpp.teamdetails.models.{Team, TrophyTypeId}
import models.stream.StreamMatchDetails
import spray.json.{JsNumber, JsObject, JsString, JsValue, JsonFormat}

case class TeamDetailsModelCH(season: Int,
                             leagueId: Int,
                             divisionLevel: Int,
                             leagueUnitId: Long,
                             leagueUnitName: String,
                             teamId: Long,
                             teamName: String,
                             round: Int,

                             powerRating: Int,
                             homeFlags: Int,
                             awayFlags: Int,
                             fanclubSize: Int,
                             trophiesNumber: Int,
                             numberOfVictories: Int,
                             numberOfUndefeated: Int)

object TeamDetailsModelCH {
  implicit val format: JsonFormat[TeamDetailsModelCH] = new JsonFormat[TeamDetailsModelCH] {
    override def read(json: JsValue): TeamDetailsModelCH = null

    override def write(obj: TeamDetailsModelCH): JsValue = {
      JsObject(
        ("season", JsNumber(obj.season)),
        ("league_id", JsNumber(obj.leagueId)),
        ("division_level", JsNumber(obj.divisionLevel)),
        ("league_unit_id", JsNumber(obj.leagueUnitId)),
        ("league_unit_name", JsString(obj.leagueUnitName)),
        ("team_id", JsNumber(obj.teamId)),
        ("team_name", JsString(obj.teamName)),
        ("round", JsNumber(obj.round)),
        ("power_rating", JsNumber(obj.powerRating)),
        ("home_flags", JsNumber(obj.homeFlags)),
        ("away_flags", JsNumber(obj.awayFlags)),
        ("fanclub_size", JsNumber(obj.fanclubSize)),
        ("trophies_number", JsNumber(obj.trophiesNumber)),
        ("number_of_victories", JsNumber(obj.numberOfVictories)),
        ("number_of_undefeated", JsNumber(obj.numberOfUndefeated)),
      )
    }
  }

  def convert(team: Team, matchDetails: StreamMatchDetails): TeamDetailsModelCH = {
    val trophyNumber = team.trophyList.count(trophy => trophy.trophyTypeId != TrophyTypeId.TOURNAMENT_WINNER
                                                    && trophy.trophyTypeId != TrophyTypeId.STUDY_TOURNNAMENT)

    TeamDetailsModelCH(
      season = matchDetails.matc.season,
      leagueId = matchDetails.matc.team.leagueUnit.league.leagueId,
      divisionLevel = matchDetails.matc.team.leagueUnit.level,
      leagueUnitId = matchDetails.matc.team.leagueUnit.leagueUnitId,
      leagueUnitName = matchDetails.matc.team.leagueUnit.leagueUnitName,
      teamId = team.teamId,
      teamName = matchDetails.matc.team.name,
      round = matchDetails.matc.round,
      powerRating = team.powerRating.powerRating,
      homeFlags = team.flags.homeFlags.size,
      awayFlags = team.flags.awayFlags.size,
      fanclubSize = team.fanclub.fanclubSize,
      trophiesNumber = trophyNumber,
      numberOfVictories = team.numberOfVictories.getOrElse(0),
      numberOfUndefeated = team.numberOfUndefeated.getOrElse(0)
    )
  }
}