package databases.requests.teamdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.model.team.CreatedSameTimeTeam
import service.DatesRange
import sqlbuilder.Select

import scala.concurrent.Future

object TeamsCreatedSameTimeRequest extends ClickhouseRequest[CreatedSameTimeTeam] {
  override val rowParser: RowParser[CreatedSameTimeTeam] = CreatedSameTimeTeam.createdSameTimeTeamMapper

  def execute(leagueId: Int, currentSeason: Int, currentRound: Int, datesRange: DatesRange)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[CreatedSameTimeTeam]] = {

    import sqlbuilder.SqlBuilder.implicits._
    val builder = Select(
        "league_id",
        "team_id",
        "team_name",
        "league_unit_id",
        "league_unit_name",
        "hatstats",
        "attack",
        "midfield",
        "defense",
        "loddar_stats",
        "tsi",
        "salary",
        "rating",
        "rating_end_of_match",
        "age",
        "injury",
        "power_rating",
        "founded"
      )
      .from("hattrick.team_rankings")
      .where
        .leagueId(leagueId)
        .season(currentSeason)
        .round(currentRound)
        .founded.greaterOrEqual(datesRange.min)
        .founded.less(datesRange.max)
      .limitBy(1, "team_id")

    restClickhouseDAO.execute(builder.sqlWithParameters().build, rowParser)
  }
}
