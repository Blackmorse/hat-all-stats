package service

import chpp.teamdetails.TeamDetailsRequest
import chpp.teamdetails.models.TeamDetails
import databases.dao.RestClickhouseDAO
import databases.requests.model.team.CreatedSameTimeTeam
import databases.requests.teamdetails.TeamsCreatedSameTimeRequest
import databases.requests.teamrankings.CompareTeamRankingsRequest
import webclients.ChppClient
import models.web.TeamComparsion
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.QueryStringBindable
import service.leagueinfo.LeagueInfoService

import java.util.Date
import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CreatedSameTimeTeamExtended(season: Int,
                                       round: Int,
                                       createdSameTimeTeam: CreatedSameTimeTeam)

object CreatedSameTimeTeamExtended {
  implicit val writes: OWrites[CreatedSameTimeTeamExtended] = Json.writes[CreatedSameTimeTeamExtended]
}


trait HattrickPeriod

case object Round extends HattrickPeriod
case object Season extends HattrickPeriod

object HattrickPeriod {
  implicit def queryStringBindable(implicit stringBuilder: QueryStringBindable[String]): QueryStringBindable[HattrickPeriod] = new QueryStringBindable[HattrickPeriod] {
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
                             chppClient: ChppClient,
                             implicit val restClickhouseDAO: RestClickhouseDAO) {

  def teamsCreatedSamePeriod(period: HattrickPeriod, foundedDate: Date,
                             leagueId: Int): Future[List[CreatedSameTimeTeamExtended]] = {
    val round = leagueInfoService.leagueInfo(leagueId).currentRound()
    val season = leagueInfoService.leagueInfo(leagueId).currentSeason()
    val ranges = seasonsService.getSeasonAndRoundRanges(foundedDate)

    val range = period match {
      case Round => ranges.roundRange
      case Season => ranges.seasonRange
    }

    TeamsCreatedSameTimeRequest.execute(leagueId, season, round, range)
      .map(list => list.map(cstt => {
        val sameTeamRanges = seasonsService.getSeasonAndRoundRanges(cstt.foundedDate)
        CreatedSameTimeTeamExtended(season = ranges.season,
          round = sameTeamRanges.round,
          createdSameTimeTeam = cstt)
      }))
  }

  def compareTwoTeams(teamId1: Long, teamId2: Long): Future[TeamComparsion] = {
    val team1Future = chppClient.execute[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(teamId1)))

    val team2Future = chppClient.execute[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(teamId2)))

    team1Future.zipWith(team2Future){case (teamDetails1, teamDetails2) =>
      val team1 = teamDetails1.teams.filter(_.teamId == teamId1).head
      val team2 = teamDetails2.teams.filter(_.teamId == teamId2).head

      if (team1.league.leagueId != team2.league.leagueId ||
          teamDetails1.user.userId == 0 || teamDetails2.user.userId == 0) {
        Future(TeamComparsion(List(), List()))
      } else {
        val teamCreateRanges1 = seasonsService.getSeasonAndRoundRanges(team1.foundedDate)
        val teamCreateRanges2 = seasonsService.getSeasonAndRoundRanges(team2.foundedDate)
        val (fromSeason, fromRound) = getCommonSeasonAndRound(teamCreateRanges1, teamCreateRanges2)

        CompareTeamRankingsRequest.execute(team1.teamId, team2.teamId,
          fromSeason, fromRound)
          .map(_.groupBy(_.teamId))
          .map(map => TeamComparsion(
            team1Rankings = map(team1.teamId).sortBy(t => (t.season, t.round)),
            team2Rankings = map(team2.teamId).sortBy(t => (t.season, t.round))
          ))
      }
    }.flatten
  }

  private def getCommonSeasonAndRound(ranges1: TeamCreatedRanges, ranges2: TeamCreatedRanges): (Int, Int) = {
    if (ranges1.season == ranges2.season) (ranges1.season, Math.max(ranges1.round, ranges2.round))
    else if (ranges1.season > ranges2.season) (ranges1.season, ranges1.round)
    else (ranges2.season, ranges2.round)
  }
}
