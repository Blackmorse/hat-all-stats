package service

import chpp.ZChppRequestExecutor.ChppErrorResponseZ
import chpp.avatars.AvatarRequest
import chpp.avatars.models.AvatarContainer
import chpp.commonmodels.MatchType
import chpp.leaguedetails.LeagueDetailsRequest
import chpp.leaguedetails.models.LeagueDetails
import chpp.leaguefixtures.LeagueFixturesRequest
import chpp.leaguefixtures.models.LeagueFixtures
import chpp.matchdetails.MatchDetailsRequest
import chpp.matchdetails.models.MatchDetails
import chpp.matches.MatchesRequest
import chpp.matches.models.Matches
import chpp.playerdetails.PlayerDetailsRequest
import chpp.playerdetails.models.PlayerDetails
import chpp.search.SearchRequest
import chpp.search.models.Search
import chpp.teamdetails.TeamDetailsRequest
import chpp.teamdetails.models.{Team, TeamDetails}
import chpp.worlddetails.WorldDetailsRequest
import chpp.worlddetails.models.{League, WorldDetails}
import controllers.NearestMatches
import databases.dao.RestClickhouseDAO
import databases.requests.ClickhouseRequest.DBIO
import databases.requests.teamrankings.HistoryTeamLeagueUnitInfoRequest
import models.clickhouse.NearestMatch
import models.web.player.AvatarPart
import models.web.{BadRequestError, HattidError, NotFoundError}
import service.leagueinfo.LeagueInfoService
import webclients.ChppClient
import zio.{IO, ZIO}

import javax.inject.{Inject, Singleton}
//TODO
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ChppService {
  type CHPPIO[A] = IO[HattidError, A]
}

@Singleton
class ChppService @Inject() (val chppClient: ChppClient,
                             val leagueInfoService: LeagueInfoService,
                             val restClickhouseDAO: RestClickhouseDAO) {
  def getTeamById(teamId: Long): IO[HattidError, Team] = {
    chppClient.executeZio[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(teamId)))
      .flatMap { teamDetails =>
        findTeamId(teamDetails, teamId) match {
          case Left(notFoundError) => ZIO.fail(notFoundError)
          case Right(team) => ZIO.succeed(team)
        }
      }
  }

  def playerDetails(playerId: Long): IO[HattidError, PlayerDetails] =
    chppClient.executeZio[PlayerDetails, PlayerDetailsRequest](PlayerDetailsRequest(playerId = playerId))
      .mapError {
        case BadRequestError(_) | ChppErrorResponseZ(_) => NotFoundError(
          entityType = "PLAYER",
          entityId = playerId.toString,
          description = s"Player not found, error from CHPP")
        case e => e
      }

  private def findTeamId(teamDetails: TeamDetails, teamId: Long): Either[NotFoundError, Team] = {
    val teamOpt = teamDetails.teams
      .find(_.teamId == teamId)
      .filter(_ => teamDetails.user.userId != 0L && teamDetails.user.userId != 13537902L)
    teamOpt match {
      case None => Left(NotFoundError(
        entityType = "TEAM",
        entityId = teamId.toString,
        description = s"The owner is a bot or has been banned"))
      case Some(team) => Right(team)
    }
  }
  
  def leagueDetails(leagueUnitId: Int): IO[HattidError, LeagueDetails] = {
    chppClient.executeZio[LeagueDetails, LeagueDetailsRequest](LeagueDetailsRequest(leagueUnitId = Some(leagueUnitId)))
      .mapError {
        // Map CHPP errors to our errors
        case BadRequestError(error) => NotFoundError( 
          entityType = "LEAGUE_UNIT",
          entityId = leagueUnitId.toString,
          description = s"League unit not found, error from CHPP: $error")
        case e => e
      }
  }

  def getDivisionLevelAndLeagueUnit(team: Team, season: Int): DBIO[(Int, Long)] = {
    for {
      league <- chppClient.executeZio[WorldDetails, WorldDetailsRequest](WorldDetailsRequest(leagueId = Some(team.league.leagueId)))
        .map(_.leagueList.head)
      result <- getDivisionLevelFromChppOrCh(league, team, season)
    } yield result
  }

  private def getDivisionLevelFromChppOrCh(league: League, team: Team, season: Int): DBIO[(Int, Long)] = {
    val htRound = league.matchRound

    if (htRound == 16
      || leagueInfoService.leagueInfo.currentSeason(team.league.leagueId) > season
      || league.season - league.seasonOffset > season) {

      HistoryTeamLeagueUnitInfoRequest.execute(season, team.league.leagueId, team.teamId)
        .map(infoOpt => {
          infoOpt.map(info => (info.divisionLevel, info.leagueUnitId))
            .getOrElse((team.leagueLevelUnit.leagueLevel, team.leagueLevelUnit.leagueLevelUnitId))
        })
    } else {
      ZIO.succeed((team.leagueLevelUnit.leagueLevel, team.leagueLevelUnit.leagueLevelUnitId.toLong))
    }
  }

  def nearestMatches(teamId: Long): Future[NearestMatches] = {
    chppClient.executeUnsafe[Matches, MatchesRequest](MatchesRequest(teamId = Some(teamId)))
      .map(response => {
        val matches = response.team.matchList
          .filter(_.matchType == MatchType.LEAGUE_MATCH)
          .map(NearestMatch.chppMatchToNearestMatch)

        val playedMatches = matches
          .filter(_.status == "FINISHED")
          .sortBy(_.matchDate)
          .takeRight(3)

        val upcomingMatches = matches.filter(_.status == "UPCOMING")
          .sortBy(_.matchDate)
          .take(3)
        NearestMatches(playedMatches, upcomingMatches)
      })
  }

  def matchDetails(matchId: Long): IO[HattidError, MatchDetails] = {
    chppClient.executeZio[MatchDetails, MatchDetailsRequest](MatchDetailsRequest(matchId = Some(matchId)))
      .mapError {
        case BadRequestError(error) => NotFoundError(
          entityType = "MATCH",
          entityId = matchId.toString,
          description = s"Match not found, error from CHPP: $error")
        case e => e
      }
  }

  def getPlayerAvatar(teamId: Int, playerId: Long): IO[HattidError, Seq[AvatarPart]] = {
    chppClient.executeZio[AvatarContainer, AvatarRequest](AvatarRequest(teamId = Some(teamId)))
      .map(avatar => {
        val player = avatar.team.players.filter(_.playerId == playerId)
          .head

        Seq(AvatarPart(player.backgroundUrl, 0, 0)) ++
          player.layers.map(layer => AvatarPart(layer.image, layer.x, layer.y))
      })
  }
  
  def search(searchRequest: SearchRequest): IO[HattidError, Search] = {
    chppClient.executeZio[Search, SearchRequest](searchRequest)
  }
  
  def leagueFixtures(leagueUnitId: Int, offsettedSeason: Int): IO[HattidError, LeagueFixtures] = {
    chppClient.executeZio[LeagueFixtures, LeagueFixturesRequest](LeagueFixturesRequest(leagueLevelUnitId = Some(leagueUnitId), season = Some(offsettedSeason)))
  }
}
