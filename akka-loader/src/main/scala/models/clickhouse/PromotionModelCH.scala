package models.clickhouse

import loadergraph.promotions.PromoteType
import models.stream.StreamTeam
import spray.json.{JsArray, JsField, JsNumber, JsObject, JsString, JsValue, JsonFormat}

import scala.collection.mutable

case class PromotionModelCH(leagueId: Int,
                            season: Int,
                            upDivisionLevel: Int,
                            promoteType: PromoteType,
                            downTeams: mutable.Buffer[StreamTeam],
                            upTeams: List[StreamTeam]) {
  def addDownTeams(downTeams: mutable.Buffer[StreamTeam]): Unit = {
    this.downTeams ++= downTeams
  }
}

object PromotionModelCH {
  implicit val format: JsonFormat[PromotionModelCH] = new JsonFormat[PromotionModelCH] {
    override def read(json: JsValue): PromotionModelCH = null

    private def teamJsValue(teamType: String, teams: Seq[StreamTeam]): List[JsField] = {
      List(
        (s"$teamType.team_id", JsArray(teams.map(team => JsNumber(team.id)).toVector)),
        (s"$teamType.team_name", JsArray(teams.map(team => JsString(team.name)).toVector)),
        (s"$teamType.division_level", JsArray(teams.map(team => JsNumber(team.leagueUnit.level)).toVector)),
        (s"$teamType.league_unit_id", JsArray(teams.map(team => JsNumber(team.leagueUnit.leagueUnitId)).toVector)),
        (s"$teamType.league_unit_name", JsArray(teams.map(team => JsString(team.leagueUnit.leagueUnitName)).toVector)),
        (s"$teamType.position", JsArray(teams.map(team => JsNumber(team.position)).toVector)),
        (s"$teamType.points", JsArray(teams.map(team => JsNumber(team.points)).toVector)),
        (s"$teamType.diff", JsArray(teams.map(team => JsNumber(team.diff)).toVector)),
        (s"$teamType.scored", JsArray(teams.map(team => JsNumber(team.scored)).toVector))
      )
    }

    override def write(obj: PromotionModelCH): JsValue = {
      val fieldList =
      List[JsField](("season", JsNumber(obj.season)),
        ("league_id", JsNumber(obj.leagueId)),
        ("up_division_level", JsNumber(obj.upDivisionLevel)),
        ("promotion_type", JsString(obj.promoteType.valueStr))) :::
      teamJsValue("going_down_teams", obj.downTeams.toList) :::
      teamJsValue("going_up_teams", obj.upTeams)
      JsObject(fieldList: _*)
    }
  }
}
