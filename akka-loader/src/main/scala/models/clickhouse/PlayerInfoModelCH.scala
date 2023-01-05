package models.clickhouse

import java.util.Date
import chpp.players.models.Player
import loadergraph.matchlineup.StreamMatchDetailsWithLineup
import models.stream.StreamMatchDetails
import spray.json.{JsNumber, JsObject, JsString, JsValue, JsonFormat}
import utils.DateTimeMarshalling.DateTimeFormat

case class PlayerInfoModelCH(season: Int,
                            leagueId: Int,
                            divisionLevel: Int,
                            leagueUnitId: Long,
                            leagueUnitName: String,
                            teamId: Long,
                            teamName: String,
                            date: Date,
                            round: Int,
                            cupLevel: Int,
                            cupLevelIndex: Int,
                            matchId: Long,

                            playerId: Long,
                            firstName: String,
                            lastName: String,
                            age: Int,
                            days: Int,
                            roleId: Int,
                            playedMinutes: Int,
                            rating: Int,
                            ratingEndOfMatch: Int,
                            //0 Bruised, -1 no injury
                            injuryLevel: Int,
                            TSI: Int,
                            salary: Int,
                            nationality: Int)

object PlayerInfoModelCH {
  implicit val format: JsonFormat[PlayerInfoModelCH] = new JsonFormat[PlayerInfoModelCH] {
    override def read(json: JsValue): PlayerInfoModelCH = null

    override def write(obj: PlayerInfoModelCH): JsValue = {
      JsObject(
        ("season", JsNumber(obj.season)),
        ("league_id", JsNumber(obj.leagueId)),
        ("division_level", JsNumber(obj.divisionLevel)),
        ("league_unit_id", JsNumber(obj.leagueUnitId)),
        ("league_unit_name", JsString(obj.leagueUnitName)),
        ("team_id", JsNumber(obj.teamId)),
        ("team_name", JsString(obj.teamName)),
        ("time", DateTimeFormat.write(obj.date)),
        ("round", JsNumber(obj.round)),
        ("cup_level", JsNumber(obj.cupLevel)),
        ("cup_level_index", JsNumber(obj.cupLevelIndex)),
        ("match_id", JsNumber(obj.matchId)),
        ("player_id", JsNumber(obj.playerId)),
        ("first_name", JsString(obj.firstName)),
        ("last_name", JsString(obj.lastName)),
        ("age", JsNumber(obj.age)),
        ("days", JsNumber(obj.days)),
        ("role_id", JsNumber(obj.roleId)),
        ("played_minutes", JsNumber(obj.playedMinutes)),
        ("rating", JsNumber(obj.rating)),
        ("rating_end_of_match", JsNumber(obj.ratingEndOfMatch)),
        ("injury_level", JsNumber(obj.injuryLevel)),
        ("tsi", JsNumber(obj.TSI)),
        ("salary", JsNumber(obj.salary)),
        ("nationality", JsNumber(obj.nationality)),
      )
    }
  }

  def convert(player: Player, matchDetails: StreamMatchDetailsWithLineup, countryMap: Map[Int, Int]): PlayerInfoModelCH = {
    val (playedMinutes,
        roleId,
        rating,
        ratingEndOfMatch) =
      player.lastMatch.flatMap(lastMatch => {
        if(lastMatch.date != null && lastMatch.date == matchDetails.matc.date) {
          Some(
            lastMatch.playedMinutes,
            lastMatch.positionCode.id,
            (lastMatch.rating * 10).toInt,
            (lastMatch.ratingEndOfMatch * 10).toInt
          )} else None
      }).getOrElse((0, 0, 0, 0))

    PlayerInfoModelCH(
      season = matchDetails.matc.season,
      leagueId = matchDetails.matc.team.leagueUnit.league.leagueId,
      divisionLevel = matchDetails.matc.team.leagueUnit.level,
      leagueUnitId = matchDetails.matc.team.leagueUnit.leagueUnitId,
      leagueUnitName = matchDetails.matc.team.leagueUnit.leagueUnitName,
      teamId = matchDetails.matc.team.id,
      teamName = matchDetails.matc.team.name,
      date = matchDetails.matc.date,
      round = matchDetails.matc.round,
      cupLevel = matchDetails.matchDetails.matc.cupLevel,
      cupLevelIndex = matchDetails.matchDetails.matc.cupLevelIndex,
      matchId = matchDetails.matc.id,
      playerId = player.playerPart.playerId,
      firstName = player.playerPart.firstName,
      lastName = player.playerPart.lastName,
      age = player.playerPart.age,
      days = player.playerPart.ageDays,
      nationality = countryMap.getOrElse(player.countryId, 0),
      playedMinutes = playedMinutes,
      roleId = roleId,
      rating = rating,
      ratingEndOfMatch = ratingEndOfMatch,
      injuryLevel = player.injuryLevel.getOrElse(-1),
      TSI = player.playerPart.TSI,
      salary = player.playerPart.salary
    )
  }
}
