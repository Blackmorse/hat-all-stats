package service

import chpp.teamdetails.models.{Team, TeamDetails}
import databases.ClickhousePool.ClickhousePool
import databases.dao.RestClickhouseDAO
import databases.requests.model.team.CreatedSameTimeTeam
import databases.requests.teamdetails.TeamsCreatedSameTimeRequest
import databases.requests.teamrankings.CompareTeamRankingsRequest
import models.web.{HattidError, TeamComparison}
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.QueryStringBindable
import service.leagueinfo.LeagueInfoServiceZIO
import webclients.AuthConfig
import zio.ZIO
import zio.http.Client

import java.util.Date
import javax.inject.Inject

case class CreatedSameTimeTeamExtended(season: Int,
                                       round: Int,
                                       createdSameTimeTeam: CreatedSameTimeTeam)

object CreatedSameTimeTeamExtended {
  implicit val writes: OWrites[CreatedSameTimeTeamExtended] = Json.writes[CreatedSameTimeTeamExtended]
}

trait HattrickPeriod

case object Round extends HattrickPeriod
case object Season extends HattrickPeriod
case class Weeks(weeksNumber: Int) extends HattrickPeriod

object HattrickPeriod {
  implicit def queryStringBindable(implicit stringBuilder: QueryStringBindable[String]): QueryStringBindable[HattrickPeriod] = new QueryStringBindable[HattrickPeriod] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, HattrickPeriod]] = {
      stringBuilder.bind("period", params)
        .map(periodEither => periodEither.flatMap(period =>{
          if (period == "round") {
            Right(Round)
          } else if (period == "season") {
            Right(Season)
          } else if(period == "weeks") {
            stringBuilder.bind("weeksNumber", params)
              .map(weeksNumberEither => weeksNumberEither.flatMap(weeksNumberString => {
                if (weeksNumberString forall Character.isDigit) {
                  Right(Weeks(weeksNumberString.toInt))
                } else {
                  Left("Invalid weeks number format")
                }
              })).getOrElse(Left("weeksNumber are not specified"))
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

class TeamsService @Inject()(seasonsService: SeasonsService) {

  def teamsCreatedSamePeriod(period: HattrickPeriod, 
                             foundedDate: Date,
                             leagueId: Int): ZIO[LeagueInfoServiceZIO & ClickhousePool & RestClickhouseDAO, HattidError, List[CreatedSameTimeTeamExtended]] = {
    val ranges = seasonsService.getSeasonAndRoundRanges(foundedDate)

    val range = period match {
      case Round => ranges.roundRange
      case Season => ranges.seasonRange
      case Weeks(weeksNumber) => seasonsService.getWeeksRange(foundedDate, weeksNumber)
    }

    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      season            <- leagueInfoService.currentSeason(leagueId)
      round             <- leagueInfoService.leagueRoundForSeason(leagueId, season)
      list              <- TeamsCreatedSameTimeRequest.execute(leagueId, season, round, range)
    } yield list.map(cstt => {
      val sameTeamRanges = seasonsService.getSeasonAndRoundRanges(cstt.foundedDate)
      CreatedSameTimeTeamExtended(season = sameTeamRanges.season,
        round = sameTeamRanges.round,
        createdSameTimeTeam = cstt)
    })
  }
  
  def compareTwoTeams(teamId1: Long, teamId2: Long): ZIO[Client & AuthConfig & ChppService & ClickhousePool & RestClickhouseDAO, HattidError, TeamComparison] = {
    val team1Zio = ZIO.serviceWithZIO[ChppService](_.getTeamById(teamId1))
    val team2Zio = ZIO.serviceWithZIO[ChppService](_.getTeamById(teamId2))
    
    for {
      (team1, team1Details, (team2, teams2Details)) <- team1Zio <&> team2Zio
      comparison                                    <- combine(team1, team1Details, team2, teams2Details)
    } yield comparison
  }
  
  private def combine(team1: Team, team1Details: TeamDetails, team2: Team, team2Details: TeamDetails): ZIO[ClickhousePool &RestClickhouseDAO, HattidError, TeamComparison] = {
    if (team1.league.leagueId != team2.league.leagueId ||
      team1Details.user.userId == 0 || team2Details.user.userId == 0) {
      ZIO.succeed(TeamComparison.empty())
    } else {
      val teamCreateRanges1 = seasonsService.getSeasonAndRoundRanges(team1.foundedDate)
      val teamCreateRanges2 = seasonsService.getSeasonAndRoundRanges(team2.foundedDate)
      val (fromSeason, fromRound) = getCommonSeasonAndRound(teamCreateRanges1, teamCreateRanges2)

      CompareTeamRankingsRequest.execute(team1.teamId, team2.teamId,
          fromSeason, fromRound)
        .map(_.groupBy(_.teamId))
        .map(map => TeamComparison(
          team1Rankings = map(team1.teamId).sortBy(t => (t.season, t.round)),
          team2Rankings = map(team2.teamId).sortBy(t => (t.season, t.round))
        ))
    }
  }

  private def getCommonSeasonAndRound(ranges1: TeamCreatedRanges, ranges2: TeamCreatedRanges): (Int, Int) = {
    if (ranges1.season == ranges2.season) (ranges1.season, Math.max(ranges1.round, ranges2.round))
    else if (ranges1.season > ranges2.season) (ranges1.season, ranges1.round)
    else (ranges2.season, ranges2.round)
  }
}
