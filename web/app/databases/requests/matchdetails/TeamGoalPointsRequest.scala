package databases.requests.matchdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.ClickhouseRequest.implicits.ClauseEntryExtended
import databases.requests.model.team.TeamGoalPoints
import databases.requests.{ClickhouseRequest, OrderingKeyPath}
import models.web.{RestStatisticsParameters, Round, SqlInjectionError}
import sqlbuilder.Select
import zio.ZIO

object TeamGoalPointsRequest extends ClickhouseRequest[TeamGoalPoints] {
  val sortingColumns: Seq[String] = Seq("won", "lost", "draw", "goals_for",
      "goals_against", "goals_difference", "points")

  override val rowParser: RowParser[TeamGoalPoints] = TeamGoalPoints.mapper

  def execute(orderingKeyPath: OrderingKeyPath,
              parameters: RestStatisticsParameters,
              playedAllMatches: Boolean,
              currentRound: Int,
              oneTeamPerUnit: Boolean): DBIO[List[TeamGoalPoints]] = wrapErrors {
    val sortBy = parameters.sortBy
    if(!sortingColumns.contains(sortBy)) {
      ZIO.fail(SqlInjectionError())
    } else {
      val (round, havingClause) = parameters.statsType match {
        case Round(r) =>
          if (playedAllMatches) {
            val matches = Math.min(r, currentRound)
            (r, Some(s"count() >= $matches"))
          } else {
            (r, None)
          }
      }
      import sqlbuilder.SqlBuilder.implicits.*
      val builder = Select(
        "any(league_id)" `as` "league",
        "team_id",
        "team_name",
        "league_unit_id",
        "league_unit_name",
        "sum(goals)" `as` "goals_for",
        "sum(enemy_goals) as goals_against",
        "goals_for - goals_against" `as` "goals_difference",
        "countIf(goals > enemy_goals)" `as` "won",
        "countIf(goals = enemy_goals)" `as` "draw",
        "countIf(goals < enemy_goals)" `as` "lost",
        "(3 * won) + draw" `as` "points"
      ).from("hattrick.match_details")
        .where
        .orderingKeyPath(orderingKeyPath)
        .season(parameters.season)
        .round.lessEqual(round)
        .isLeagueMatch
        .groupBy(
          "team_name", "team_id", "league_unit_id", "league_unit_name"
        )
        .having.and(havingClause)
        .orderBy(
          sortBy.to(parameters.sortingDirection.toSql),
          "goals_difference".to(parameters.sortingDirection.toSql),
          "team_id".to(parameters.sortingDirection.toSql)
        ).limit(page = parameters.page, pageSize = parameters.pageSize)
        //TODO add Option for limitBy. 12 just greater than 8 - maximum for leagueUnit
        .limitBy(if (oneTeamPerUnit) 1 else 12, "league_unit_id")

      for {
        restClickhouseDAO <- ZIO.service[RestClickhouseDAO]
        result <- restClickhouseDAO.executeZIO(builder.sqlWithParameters().build, rowParser)
      } yield result
    }
  }
}
