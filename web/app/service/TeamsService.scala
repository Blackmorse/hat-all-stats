package service

import databases.dao.RestClickhouseDAO
import databases.requests.model.team.CreatedSameTimeTeam
import databases.requests.teamdetails.TeamsCreatedSameTimeRequest
import databases.requests.teamrankings.CompareTeamRankingsRequest
import hattrick.Hattrick
import models.clickhouse.TeamRankings
import models.web.TeamComparsion
import play.api.libs.json.Json
import play.api.mvc.QueryStringBindable
import service.leagueinfo.LeagueInfoService

import java.util.Date
import javax.inject.Inject
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class CreatedSameTimeTeamExtended(season: Int,
                                       round: Int,
                                       createdSameTimeTeam: CreatedSameTimeTeam)

object CreatedSameTimeTeamExtended {
  implicit val writes = Json.writes[CreatedSameTimeTeamExtended]
}


trait HattrickPeriod

case object Round extends HattrickPeriod
case object Season extends HattrickPeriod

object HattrickPeriod {
  implicit def queryStringBindable(implicit stringBuilder: QueryStringBindable[String]) = new QueryStringBindable[HattrickPeriod] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, HattrickPeriod]] = {
      stringBuilder.bind("period", params)
        .map(periodEither => periodEither.flatMap(period =>{
          if (period == "round") {
            Right(Round)
          } else if (period == "season") {
            Right(Season)
          } else {
            Left("Unknown period type. Available values: season, round")
          }
        }))
    }

    override def unbind(key: String, value: HattrickPeriod): String = {
      val valueStr = value match {
        case Season => "season"
        case Round => "round"
      }
      stringBuilder.unbind("period", valueStr)
    }
  }
}

class TeamsService @Inject()(leagueInfoService: LeagueInfoService,
                             seasonsService: SeasonsService,
                             hattrick: Hattrick,
                             implicit val restClickhouseDAO: RestClickhouseDAO) {

  def teamsCreatedSamePeriod(period: HattrickPeriod, foundedDate: Date,
                             leagueId: Int): Future[List[CreatedSameTimeTeamExtended]] = {
    val round = leagueInfoService.leagueInfo(leagueId).currentRound()
    val ranges = seasonsService.getSeasonAndRoundRanges(foundedDate)

    val range = period match {
      case Round => ranges.roundRange
      case Season => ranges.seasonRange
    }

    TeamsCreatedSameTimeRequest.execute(leagueId, round, range)
      .map(list => list.map(cstt => {
        val sameTeamRanges = seasonsService.getSeasonAndRoundRanges(cstt.foundedDate)
        CreatedSameTimeTeamExtended(season = ranges.season,
          round = sameTeamRanges.round,
          createdSameTimeTeam = cstt)
      }))
  }

  def compareTwoTeams(teamId1: Long, teamId2: Long): Future[TeamComparsion] = {
    val team1Future = Future(hattrick.api.teamDetails().teamID(teamId1).execute()
      .getTeams.asScala
      .filter(_.getTeamId == teamId1)
      .head)

    val team2Future = Future(hattrick.api.teamDetails().teamID(teamId2).execute()
      .getTeams.asScala
      .filter(_.getTeamId == teamId2)
      .head)

    team1Future.zipWith(team2Future){case (team1, team2) =>
      if (team1.getLeague.getLeagueId != team2.getLeague.getLeagueId) {
        throw new RuntimeException("unable to compare teams from different countries")
      }
      val teamCreateRanges1 = seasonsService.getSeasonAndRoundRanges(team1.getFoundedDate)
      val teamCreateRanges2 = seasonsService.getSeasonAndRoundRanges(team2.getFoundedDate)
      val (fromSeason, fromRound) = getCommonSeasonAndRound(teamCreateRanges1, teamCreateRanges2)

      CompareTeamRankingsRequest.execute(team1.getTeamId, team2.getTeamId,
        fromSeason, fromRound)
      .map(_.groupBy(_.teamId))
      .map(map => TeamComparsion(
        team1Rankings = map(team1.getTeamId).sortBy(t => (t.season, t.round)),
        team2Rankings = map(team2.getTeamId).sortBy(t => (t.season, t.round))
      ))
    }.flatten
  }

  private def getCommonSeasonAndRound(ranges1: TeamCreatedRanges, ranges2: TeamCreatedRanges): (Int, Int) = {
    if (ranges1.season == ranges2.season) (ranges1.season, Math.max(ranges1.round, ranges2.round))
    else if (ranges1.season > ranges2.season) (ranges1.season, ranges1.round)
    else (ranges2.season, ranges2.round)
  }
}
