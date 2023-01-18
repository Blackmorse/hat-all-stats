package service

import chpp.avatars.AvatarRequest
import chpp.avatars.models.AvatarContainer
import chpp.chpperror.ChppError
import chpp.commonmodels.MatchType
import chpp.matches.MatchesRequest
import chpp.matches.models.Matches
import chpp.playerdetails.PlayerDetailsRequest
import chpp.playerdetails.models.PlayerDetails
import chpp.teamdetails.TeamDetailsRequest
import chpp.teamdetails.models.{Team, TeamDetails}
import chpp.worlddetails.WorldDetailsRequest
import chpp.worlddetails.models.WorldDetails
import controllers.NearestMatches
import databases.dao.RestClickhouseDAO
import databases.requests.teamrankings.HistoryTeamLeagueUnitInfoRequest
import models.clickhouse.NearestMatch
import models.web.NotFoundError
import models.web.player.AvatarPart
import service.leagueinfo.LeagueInfoService
import webclients.ChppClient

import javax.inject.{Inject, Singleton}
//TODO
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ChppService @Inject() (val chppClient: ChppClient,
                             val leagueInfoService: LeagueInfoService,
                             implicit val restClickhouseDAO: RestClickhouseDAO) {


  def getTeamById(teamId: Long): Future[Either[NotFoundError, Team]] = {
    chppClient.execute[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(teamId)))
      .map {
        case Left(chppError) => Left(NotFoundError(
          entityType = NotFoundError.TEAM,
          entityId = teamId.toString,
          description = chppError.error))
        case Right(teamDetails) => findTeamId(teamDetails, teamId)
      }
  }

  private def findTeamId(teamDetails: TeamDetails, teamId: Long): Either[NotFoundError, Team] = {
    println(teamId)
    println(teamDetails)
    val teamOpt = teamDetails.teams
      .find(_.teamId == teamId)
      .filter(_ => teamDetails.user.userId != 0L && teamDetails.user.userId != 13537902L)
    println(teamOpt)
    teamOpt match {
      case None => Left(NotFoundError(
        entityType = "TEAM",
        entityId = teamId.toString,
        description = s"The owner is a bot or has been banned"))
      case Some(team) => Right(team)
    }
  }

  @deprecated
  def getTeamByIdUnsafe(teamId: Long): Future[Option[Team]] = {
    chppClient.executeUnsafe[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(teamId)))
      .map(teamDetails => {
        teamDetails.teams
          .find(_.teamId == teamId)
          .filter(_ => teamDetails.user.userId != 0L && teamDetails.user.userId != 13537902L)
      })
  }

  def getDivisionLevelAndLeagueUnit(team: Team, season: Int): Future[(Int, Long)] = {
    chppClient.executeUnsafe[WorldDetails, WorldDetailsRequest](WorldDetailsRequest(leagueId = Some(team.league.leagueId)))
      .map(_.leagueList.head)
      .flatMap(league => {
        val htRound = league.matchRound

        if(htRound == 16
          || leagueInfoService.leagueInfo.currentSeason(team.league.leagueId) > season
          || league.season - league.seasonOffset > season) {

          HistoryTeamLeagueUnitInfoRequest.execute(season, team.league.leagueId, team.teamId)
            .map(infoOpt => {
              infoOpt.map(info => (info.divisionLevel, info.leagueUnitId))
                .getOrElse((team.leagueLevelUnit.leagueLevel, team.leagueLevelUnit.leagueLevelUnitId))
            })
        } else {
          Future((team.leagueLevelUnit.leagueLevel, team.leagueLevelUnit.leagueLevelUnitId.toLong))
        }
      })
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

  def playerDetails(playerId: Long): Future[Either[ChppError, PlayerDetails]] =
    chppClient.execute[PlayerDetails, PlayerDetailsRequest] (PlayerDetailsRequest(playerId = playerId))

  def getPlayerAvatar(teamId: Int, playerId: Long): Future[Seq[AvatarPart]] = {
    chppClient.executeUnsafe[AvatarContainer, AvatarRequest](AvatarRequest(teamId = Some(teamId)))
      .map(avatar => {
        val player = avatar.team.players.filter(_.playerId == playerId)
          .head

        Seq(AvatarPart(player.backgroundUrl, 0, 0)) ++
          player.layers.map(layer => AvatarPart(layer.image, layer.x, layer.y))
      })
  }
}
