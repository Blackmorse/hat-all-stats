package databases.requests.overview

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseOverviewRequest
import databases.requests.overview.model.NumberOverview
import databases.sqlbuilder.SqlBuilder

import scala.concurrent.Future

object NumberOverviewRequest extends ClickhouseOverviewRequest[NumberOverview] {
  override val sql: String = """
                               |SELECT
                               |  uniq(team_id) as numberOfTeams,
                               |  count() as numberOfPlayers,
                               |  countIf(injury_level > 0) AS injuried,
                               |  sum(goals) as goals,
                               |  sum(yellow_cards) as yellow_cards,
                               |  sum(red_cards) as red_cards
                               |FROM hattrick.player_stats
                               |  __where__""".stripMargin

  override val rowParser: RowParser[NumberOverview] = NumberOverview.mapper

  override def execute(season: Int, round: Int, leagueId: Option[Int], divisionLevel: Option[Int])
                      (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[NumberOverview]] = {
    import SqlBuilder.implicits._

    val builder = SqlBuilder("", newApi = true)
      .select(
        "uniq(team_id)" as "numberOfTeams",
        "count()" as "numberOfPlayers",
        "countIf(injury_level > 0)" as "injuried",
        "sum(goals)" as "goals",
        "sum(yellow_cards)" as "yellow_cards",
        "sum(red_cards)" as "red_cards"
      )
      .from("hattrick.player_stats")
      .where
        .round(round)
        .season(season)
        .leagueId(leagueId)
        .divisionLevel(divisionLevel)
        .isLeagueMatch

      restClickhouseDAO.execute(builder.build, rowParser)
  }
}
