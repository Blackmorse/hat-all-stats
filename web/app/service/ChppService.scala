package service

import chpp.avatars.AvatarRequest
import chpp.avatars.models.AvatarContainer
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
  def getTeamById(teamId: Long): Future[Either[Team, Team]] = {
    chppClient.execute[TeamDetails, TeamDetailsRequest](TeamDetailsRequest(teamId = Some(teamId)))
      .map(teamDetails => {
        val team = teamDetails.teams.filter(_.teamId == teamId).head
        if (teamDetails.user.userId == 0L) Left(team)
        else Right(team)
      })
  }

  def getDivisionLevelAndLeagueUnit(team: Team, season: Int): Future[(Int, Long)] = {
    chppClient.execute[WorldDetails, WorldDetailsRequest](WorldDetailsRequest(leagueId = Some(team.league.leagueId)))
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
    chppClient.execute[Matches, MatchesRequest](MatchesRequest(teamId = Some(teamId)))
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

  def playerDetails(playerId: Long): Future[PlayerDetails] =
    chppClient.execute[PlayerDetails, PlayerDetailsRequest] (PlayerDetailsRequest(playerId = playerId))

  def getPlayerAvatar(teamId: Int, playerId: Long): Future[Seq[AvatarPart]] = {
    chppClient.execute[AvatarContainer, AvatarRequest](AvatarRequest(teamId = Some(teamId)))
      .map(avatar => {
        val player = avatar.team.players.filter(_.playerId == playerId)
          .head

        Seq(AvatarPart(player.backgroundUrl, 0, 0)) ++
          player.layers.map(layer => AvatarPart(layer.image, layer.x, layer.y))
      })
  }
}
