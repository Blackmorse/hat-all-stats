package com.blackmorse.hattid.web.service

import chpp.teamdetails.models.{Team, TeamDetails}
import com.blackmorse.hattid.web.databases.requests.model.team.CreatedSameTimeTeam
import com.blackmorse.hattid.web.databases.requests.teamdetails.TeamsCreatedSameTimeRequest
import com.blackmorse.hattid.web.databases.requests.teamrankings.CompareTeamRankingsRequest
import com.blackmorse.hattid.web.models.web.{HattidError, TeamComparison}
import com.blackmorse.hattid.web.service.leagueinfo.LeagueInfoServiceZIO
import com.blackmorse.hattid.web.zios.{CHPPServices, DBServices}
import zio.ZIO
import zio.http.Client
import zio.json.{DeriveJsonEncoder, JsonEncoder}

import java.text.SimpleDateFormat
import java.util.Date

case class CreatedSameTimeTeamExtended(season: Int,
                                       round: Int,
                                       createdSameTimeTeam: CreatedSameTimeTeam)

object CreatedSameTimeTeamExtended {
  implicit val jsonEncoder: JsonEncoder[CreatedSameTimeTeamExtended] = DeriveJsonEncoder.gen[CreatedSameTimeTeamExtended]
}

trait HattrickPeriod

case object Round extends HattrickPeriod
case object Season extends HattrickPeriod
case class Weeks(weeksNumber: Int) extends HattrickPeriod

class TeamsService{

  private val firstSeasonFirstDate = new SimpleDateFormat("yyyy-MM-dd").parse("1997-09-28") //1 season 1 day!

  private val weekMs: Long = 1000L * 3600 * 24 * 7
  private val seasonMs = weekMs * 16

  def teamsCreatedSamePeriod(period: HattrickPeriod, 
                             foundedDate: Date,
                             leagueId: Int): ZIO[LeagueInfoServiceZIO & DBServices, HattidError, List[CreatedSameTimeTeamExtended]] = {
    val ranges = getSeasonAndRoundRanges(foundedDate)

    val range = period match {
      case Round => ranges.roundRange
      case Season => ranges.seasonRange
      case Weeks(weeksNumber) => getWeeksRange(foundedDate, weeksNumber)
    }

    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      season            <- leagueInfoService.currentSeason(leagueId)
      round             <- leagueInfoService.leagueRoundForSeason(leagueId, season)
      list              <- TeamsCreatedSameTimeRequest.execute(leagueId, season, round, range)
    } yield list.map(cstt => {
      val sameTeamRanges = getSeasonAndRoundRanges(cstt.foundedDate)
      CreatedSameTimeTeamExtended(season = sameTeamRanges.season,
        round = sameTeamRanges.round,
        createdSameTimeTeam = cstt)
    })
  }
  
  def compareTwoTeams(teamId1: Long, teamId2: Long): ZIO[CHPPServices & DBServices, HattidError, TeamComparison] = {
    val team1Zio = ZIO.serviceWithZIO[ChppService](_.getTeamById(teamId1))
    val team2Zio = ZIO.serviceWithZIO[ChppService](_.getTeamById(teamId2))
    
    for {
      (team1, team1Details, (team2, teams2Details)) <- team1Zio <&> team2Zio
      comparison                                    <- combine(team1, team1Details, team2, teams2Details)
    } yield comparison
  }
  
  private def combine(team1: Team, team1Details: TeamDetails, team2: Team, team2Details: TeamDetails): ZIO[DBServices, HattidError, TeamComparison] = {
    if (team1.league.leagueId != team2.league.leagueId ||
      team1Details.user.userId == 0 || team2Details.user.userId == 0) {
      ZIO.succeed(TeamComparison.empty())
    } else {
      val teamCreateRanges1 = getSeasonAndRoundRanges(team1.foundedDate)
      val teamCreateRanges2 = getSeasonAndRoundRanges(team2.foundedDate)
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

  private def getSeasonAndRoundFromDate(date: Date): (Int, Int) = {
    val season = (date.getTime - firstSeasonFirstDate.getTime) / seasonMs + 1
    val round = ((date.getTime - firstSeasonFirstDate.getTime) % seasonMs) / weekMs + 1
    (season.toInt, round.toInt)
  }

  private def getSeasonAndRoundRanges(date: Date): TeamCreatedRanges = {
    val (season, round) = getSeasonAndRoundFromDate(date)

    val firstDayOfSeasonMs = firstSeasonFirstDate.getTime + (season - 1) * seasonMs
    val firstDayOfSeason = new Date(firstDayOfSeasonMs)

    val firstDayOfNextSeason = new Date(firstDayOfSeasonMs + seasonMs)

    val firstDayOfRoundMs = firstDayOfSeasonMs + (round - 1) * weekMs
    val firstDayOfRound = new Date(firstDayOfRoundMs)

    val firstDayOfNextRound = new Date(firstDayOfRoundMs + weekMs)

    TeamCreatedRanges(
      season = season,
      round = round,
      seasonRange = DatesRange(firstDayOfSeason, firstDayOfNextSeason),
      roundRange = DatesRange(firstDayOfRound, firstDayOfNextRound))
  }

  private def getWeeksRange(date: Date, weeksNumber: Int): DatesRange = {
    val minDate = new Date(date.getTime - weekMs * weeksNumber)
    val maxDate = new Date(date.getTime + weekMs * weeksNumber)

    DatesRange(minDate, maxDate)
  }
}
