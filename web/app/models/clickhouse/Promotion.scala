package models.clickhouse

import anorm.SqlParser.get
import anorm.~
import play.api.libs.json.Json

case class PromoteTeam(teamId: Long, teamName: String, divisionLevel: Int, leagueUnitId: Long,
                       leagueUnitName: String, position: Int, points: Int, diff: Int, scored: Int)

case class Promotion (season: Int, leagueId: Int, upDivisionLevel: Int, promoteType: String,
                      downTeams: Array[PromoteTeam], upTeams: Array[PromoteTeam])

object Promotion {
  implicit val promoteTeamWrites = Json.writes[PromoteTeam]
  implicit val writes = Json.writes[Promotion]

  val promotionMapper = {
    get[Int]("season") ~
    get[Int]("league_id") ~
    get[Int]("up_division_level") ~
    get[String]("promotion_type") ~
    get[Array[Long]]("going_down_teams.team_id") ~
    get[Array[String]]("going_down_teams.team_name") ~
    get[Array[Int]]("going_down_teams.division_level") ~
    get[Array[Long]]("going_down_teams.league_unit_id") ~
    get[Array[String]]("going_down_teams.league_unit_name") ~
    get[Array[Int]]("going_down_teams.position") ~
    get[Array[Int]]("going_down_teams.points") ~
    get[Array[Int]]("going_down_teams.diff") ~
    get[Array[Int]]("going_down_teams.scored") ~
    get[Array[Long]]("going_up_teams.team_id") ~
    get[Array[String]]("going_up_teams.team_name") ~
    get[Array[Int]]("going_up_teams.division_level") ~
    get[Array[Long]]("going_up_teams.league_unit_id") ~
    get[Array[String]]("going_up_teams.league_unit_name") ~
    get[Array[Int]]("going_up_teams.position") ~
    get[Array[Int]]("going_up_teams.points") ~
    get[Array[Int]]("going_up_teams.diff") ~
    get[Array[Int]]("going_up_teams.scored") map {
      case season ~ leagueId ~ upDivisionLevel ~ promoteType ~
        downTeamIds ~ downTeamNames ~ downTeamDivisionLevels ~ downTeamLeagueUnitIds ~ downTeamLeagueUnitNames ~
            downTeamPositions ~ downTeamPoints ~ downTeamDiffs ~ downTeamScores ~
        upTeamIds ~ upTeamNames ~ upTeamDivisionLevels ~ upTeamLeagueUnitIds ~ upTeamLeagueUnitNames ~
            upTeamPositions ~ upTeamPoints ~ upTeamDiffs ~ upTeamScores =>

        Promotion(season, leagueId, upDivisionLevel, promoteType,
          zip(downTeamIds, downTeamNames, downTeamDivisionLevels, downTeamLeagueUnitIds, downTeamLeagueUnitNames,
            downTeamPositions, downTeamPoints, downTeamDiffs, downTeamScores),
          zip(upTeamIds, upTeamNames, upTeamDivisionLevels, upTeamLeagueUnitIds, upTeamLeagueUnitNames,
            upTeamPositions, upTeamPoints, upTeamDiffs, upTeamScores))

    }
  }

  def zip(teamIds: Array[Long], teamNames: Array[String], division_levels: Array[Int], leagueUnitIds: Array[Long],
          leagueUnitNames: Array[String], positions: Array[Int], points: Array[Int], diffs: Array[Int], scores: Array[Int]): Array[PromoteTeam] = {
      teamIds.zip(teamNames).zip(division_levels).zip(leagueUnitIds).zip(leagueUnitNames).zip(positions).zip(points).zip(diffs).zip(scores)
      .map{case((((((((teamId, teamName), divisionLevel), leagueUnitId), leagueUnitName), position), point), diff), scored) =>
        PromoteTeam(teamId, teamName, divisionLevel, leagueUnitId, leagueUnitName, position, point, diff, scored)}
  }
}