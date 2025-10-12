package service

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
import chpp.translations.TranslationsRequest
import chpp.translations.models.Translations
import chpp.worlddetails.WorldDetailsRequest
import chpp.worlddetails.models.{League, WorldDetails}
import controllers.NearestMatches
import databases.ClickhousePool.ClickhousePool
import databases.dao.RestClickhouseDAO
import databases.requests.teamrankings.HistoryTeamLeagueUnitInfoRequest
import models.clickhouse.NearestMatch
import models.web.player.AvatarPart
import models.web.{BadRequestError, HattidError, NotFoundError}
import service.leagueinfo.LeagueInfoServiceZIO
import webclients.{AuthConfig, ChppClient}
import zio.http.Client
import zio.{IO, ZIO}

import javax.inject.{Inject, Singleton}
//TODO

object ChppService {
  type CHPPIO[A] = IO[HattidError, A]
}

@Singleton
class ChppService @Inject() (val chppClient: ChppClient) {
  def getTeamById(teamId: Long): ZIO[Client & AuthConfig, HattidError, (Team, TeamDetails)] = {
    chppClient.executeZio[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(teamId)))
      .flatMap { teamDetails =>
        findTeamId(teamDetails, teamId) match {
          case Left(notFoundError) => ZIO.fail(notFoundError)
          case Right(team) => ZIO.succeed((team, teamDetails))
        }
      }
  }
  
  def getTeamsSimple(teamId: Long): ZIO[Client & AuthConfig, HattidError, TeamDetails] =
    chppClient.executeZio[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(teamId)))

  def playerDetails(playerId: Long): ZIO[Client & AuthConfig, HattidError, PlayerDetails] =
    chppClient.executeZio[PlayerDetails, PlayerDetailsRequest](PlayerDetailsRequest(playerId = playerId))
      .mapError {
        case BadRequestError(_) => NotFoundError(
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
  
  def leagueDetails(leagueUnitId: Int): ZIO[Client & AuthConfig, HattidError, LeagueDetails] = {
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
  
  def getWorldDetails(): ZIO[Client & AuthConfig, HattidError, WorldDetails] =
    chppClient.executeZio[WorldDetails, WorldDetailsRequest](WorldDetailsRequest())
    
    
  def getDivisionLevelAndLeagueUnit(team: Team, season: Int): ZIO[Client & AuthConfig & ClickhousePool & RestClickhouseDAO & LeagueInfoServiceZIO, HattidError, (Int, Long)] = {
    for {
      league <- chppClient.executeZio[WorldDetails, WorldDetailsRequest](WorldDetailsRequest(leagueId = Some(team.league.leagueId)))
        .map(_.leagueList.head)
      result <- getDivisionLevelFromChppOrCh(league, team, season)
    } yield result
  }

  private def getDivisionLevelFromChppOrCh(league: League, team: Team, season: Int): ZIO[Client & ClickhousePool & RestClickhouseDAO & LeagueInfoServiceZIO, HattidError, (Int, Long)] = {
    val htRound = league.matchRound
    
    val currentSeasonZIO = for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      currentSeason     <- leagueInfoService.currentSeason(team.league.leagueId)
    } yield currentSeason

    currentSeasonZIO.flatMap { currentSeason =>
      if (htRound == 16
        || currentSeason > season
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
  }
  
  def nearestMatches(teamId: Long): ZIO[Client & AuthConfig, HattidError, NearestMatches] = {
    for {
      response   <- chppClient.executeZio[Matches, MatchesRequest](MatchesRequest(teamId = Some(teamId)))
    } yield {
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
    }
  }
  
  def matches(teamId: Long): ZIO[Client & AuthConfig, HattidError, Matches] = {
    chppClient.executeZio[Matches, MatchesRequest](MatchesRequest(teamId = Some(teamId)))
  }

  def currentTeamPlayedMatchesAndUpcomingOpponents(teamId: Long): ZIO[Client & AuthConfig, HattidError, (Seq[NearestMatch], Seq[(Long, String)])] = {
    chppClient.executeZio[Matches, MatchesRequest](MatchesRequest(teamId = Some(teamId)))
      .map(matches => {
        val currentTeamPlayedMatches = matches.team.matchList
          .filter(m => m.matchType == MatchType.LEAGUE_MATCH || m.matchType == MatchType.CUP_MATCH)
          .filter(_.status == "FINISHED")
          .sortBy(_.matchDate)
          .takeRight(3)
          .map(NearestMatch.chppMatchToNearestMatch)

        val currentTeamNextOpponents = matches.team.matchList
          .filter(m => m.matchType == MatchType.LEAGUE_MATCH || m.matchType == MatchType.CUP_MATCH)
          .filter(_.status == "UPCOMING")
          .sortBy(_.matchDate)
          .take(3)
          .map(matc => {
            if (matc.homeTeam.homeTeamId == teamId) {
              (matc.awayTeam.awayTeamId, matc.awayTeam.awayTeamName)
            } else {
              (matc.homeTeam.homeTeamId, matc.homeTeam.homeTeamName)
            }
          })

        (currentTeamPlayedMatches, currentTeamNextOpponents)
      })
  }

  def matchDetails(matchId: Long): ZIO[Client & AuthConfig, HattidError, MatchDetails] = {
    chppClient.executeZio[MatchDetails, MatchDetailsRequest](MatchDetailsRequest(matchId = Some(matchId)))
      .mapError {
        case BadRequestError(error) => NotFoundError(
          entityType = "MATCH",
          entityId = matchId.toString,
          description = s"Match not found, error from CHPP: $error")
        case e => e
      }
  }

  def getPlayerAvatar(teamId: Int, playerId: Long): ZIO[Client & AuthConfig, HattidError, Seq[AvatarPart]] = {
    chppClient.executeZio[AvatarContainer, AvatarRequest](AvatarRequest(teamId = Some(teamId)))
      .map(avatar => {
        val player = avatar.team.players.filter(_.playerId == playerId)
          .head

        Seq(AvatarPart(player.backgroundUrl, 0, 0)) ++
          player.layers.map(layer => AvatarPart(layer.image, layer.x, layer.y))
      })
  }
  
  def search(searchRequest: SearchRequest): ZIO[Client & AuthConfig, HattidError, Search] = {
    chppClient.executeZio[Search, SearchRequest](searchRequest)
  }
  
  def leagueFixtures(leagueUnitId: Int, offsettedSeason: Int): ZIO[Client & AuthConfig, HattidError, LeagueFixtures] = {
    chppClient.executeZio[LeagueFixtures, LeagueFixturesRequest](LeagueFixturesRequest(leagueLevelUnitId = Some(leagueUnitId), season = Some(offsettedSeason)))
  }
  
  def translations(languageId: Int): ZIO[Client & AuthConfig, HattidError, Translations] = 
    chppClient.executeZio[Translations, TranslationsRequest](TranslationsRequest(languageId = languageId))
}
