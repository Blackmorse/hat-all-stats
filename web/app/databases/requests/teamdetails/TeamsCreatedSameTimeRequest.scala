package databases.requests.teamdetails

import anorm.RowParser
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest
import databases.requests.model.team.CreatedSameTimeTeam
import databases.sqlbuilder.SqlBuilder
import service.DatesRange

import scala.concurrent.Future

object TeamsCreatedSameTimeRequest extends ClickhouseRequest[CreatedSameTimeTeam] {
  override val rowParser: RowParser[CreatedSameTimeTeam] = CreatedSameTimeTeam.createdSameTimeTeamMapper

  def execute(leagueId: Int, currentRound: Int, datesRange: DatesRange)
             (implicit restClickhouseDAO: RestClickhouseDAO): Future[List[CreatedSameTimeTeam]] = {

    import SqlBuilder.implicits._
    val builder = SqlBuilder("", newApi = true)
      .select(
        "league_id",
        "team_id",
        "team_name",
        "league_unit_id",
        "league_unit_name",
        "founded_date",
        "power_rating"
      )
      .from("hattrick.team_details")
      .where
        .leagueId(leagueId)
        .round(currentRound)
        .foundedDate.greaterOrEqual(datesRange.min)
        .foundedDate.less(datesRange.max)

    restClickhouseDAO.execute(builder.build, rowParser)
  }
}
