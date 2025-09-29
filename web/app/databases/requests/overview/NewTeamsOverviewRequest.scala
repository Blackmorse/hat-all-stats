package databases.requests.overview

import anorm.RowParser
import databases.requests.ClickhouseOverviewRequest
import databases.requests.model.overview.NumberOverviewTeamDetails
import hattid.CommonData
import sqlbuilder.{SqlBuilder, WSelect, With}

object NewTeamsOverviewRequest extends ClickhouseOverviewRequest[NumberOverviewTeamDetails] {
  override val rowParser: RowParser[NumberOverviewTeamDetails] = NumberOverviewTeamDetails.mapper

  override protected def builder(season: Int,
                                 round: Int,
                                 leagueId: Option[Int],
                                 divisionLevel: Option[Int]): SqlBuilder = {
    import SqlBuilder.implicits._

    val leagueDay = s"dt - (($season - season) * 16 * 7) - (($round - round) * 7)"

    With(
      WSelect(
        "max(dt)"
      ) .from("hattrick.match_details")
        .where
          .leagueId(leagueId.getOrElse(CommonData.LAST_SERIES_LEAGUE_ID))
          .divisionLevel(divisionLevel)
          .season(season)
          .round(round)
          .isLeagueMatch
    ).as("dt")
      .select(
          s"countIf($leagueDay - founded_date < multiIf(round = 1, 21, 7))" `as` "numberOfNewTeams"
        )
          .from("hattrick.team_details")
          .where
            .round(round)
            .season(season)
            .leagueId(leagueId)
            .divisionLevel(divisionLevel)
  }
}
