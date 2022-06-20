package tests

import chpp.worlddetails.models.WorldDetails
import sqlbuilder.Select
import sqlbuilder.functions.uniqExact
import tests.models.Count

import java.sql.DriverManager

object ClickhouseTests {

  def testNoHolesInLeagueRounds(worldDetails: WorldDetails)(implicit connection: java.sql.Connection): Unit = {
    import sqlbuilder.SqlBuilder.implicits._

    val season = worldDetails.leagueList.find(_.leagueId == 1000).map(league => league.season - league.seasonOffset).get
    val round = worldDetails.leagueList.find(_.leagueId == 1000).get.matchRound - 1

    val builder = Select(
      "league_id",
      "division_level",
      "round",
      "count()" as "cnt"
    ).from("hattrick.match_details")
      .where
      .season(season)
      .isLeagueMatch
      .groupBy("league_id", "division_level", "round")

    val results = builder.sqlWithParameters().build.as(Count.mapper.*)
    results.groupBy(_.leagueId).foreach{ case (_, leagueCounts) =>
      leagueCounts.groupBy(_.divisionLevel).foreach{case (_, divisionLevelCounts) =>
        if(!(1 to round).forall(r => divisionLevelCounts.exists(_.round == r))) {
          throw new Exception(s"Inconsistency: $divisionLevelCounts")
        }
        for (r <- 2 to round) {
          val previousRoundCount = divisionLevelCounts.find(_.round == r - 1).get.cnt
          val currentRoundCount = divisionLevelCounts.find(_.round == r).get.cnt

          if(Math.abs(previousRoundCount - currentRoundCount) > currentRoundCount * 0.3 && currentRoundCount > 8) {
            throw new Exception(s"Suspicious teams number difference for rounds ${r - 1} and $r: ${divisionLevelCounts.sortBy(_.round)}")
          }
        }
      }
    }
  }

  def testNumberOfTeamRankingsRecords(worldDetails: WorldDetails): Unit = {
    import sqlbuilder.SqlBuilder.implicits._
    implicit val connection: java.sql.Connection = DriverManager.getConnection("jdbc:clickhouse://localhost:8123")

    val season = worldDetails.leagueList.find(_.leagueId == 1000).map(league => league.season - league.seasonOffset).get
    val round = worldDetails.leagueList.find(_.leagueId == 1000).get.matchRound - 1

    val builderFunc = (table: String, checkForLeagueMatch: Boolean) => {
      val builder = Select(
        "league_id",
        "division_level",
        "round",
        "count()" as "cnt")
        .from(table)
        .where
        .season(season)
        .round(round)

      val result = if (checkForLeagueMatch) builder.isLeagueMatch else builder
      result.groupBy("league_id", "division_level", "round")
    }

    val matchDetailsBuilder = builderFunc("hattrick.match_details", true)
    val teamRankingsBuilder = builderFunc("hattrick.team_rankings", false)

    val matchDetailsCounts = matchDetailsBuilder.sqlWithParameters().build
      .as(Count.mapper.*)
    val teamRankingsCounts = teamRankingsBuilder.sqlWithParameters().build
      .as(Count.mapper.*)

    if (!isNumbersEqual(matchDetailsCounts.size, teamRankingsCounts.size)) {
      throw new Exception(s"Counts doesn't match while test of team rankings consistency: " +
        s"matchDetails: ${matchDetailsCounts.size}, teamRankings: ${teamRankingsCounts.size}")
    }

    matchDetailsCounts.sortBy(count => (count.leagueId, count.divisionLevel)).zip(teamRankingsCounts.sortBy(count => (count.leagueId, count.divisionLevel)))
      .foreach{ case (matchDetailsCount, teamRankingsCount) =>
        if (matchDetailsCount.cnt * 2 != teamRankingsCount.cnt) {
          throw new Exception(s"MatchDetails and TeamRankings for ${matchDetailsCount.leagueId} division level ${matchDetailsCount.divisionLevel} doesn't match: " +
          s"match details: ${matchDetailsCount.cnt}, " +
          s"team rankings: ${teamRankingsCount.cnt}")
        }
      }
  }

  def testTeamCounts(worldDetails: WorldDetails): Unit = {

    import sqlbuilder.SqlBuilder.implicits._
    implicit val connection: java.sql.Connection = DriverManager.getConnection("jdbc:clickhouse://localhost:8123")

    val season = worldDetails.leagueList.find(_.leagueId == 1000).map(league => league.season - league.seasonOffset).get
    val round = worldDetails.leagueList.find(_.leagueId == 1000).get.matchRound - 1

    val uniqTeamsBuilderFunc = (table: String) =>
      Select(
        "league_id",
        "division_level",
        "round",
        uniqExact("team_id") as "cnt")
        .from(table)
        .groupBy("league_id", "division_level", "round")
        .where
        .season(season)
        .round(round)

    val teamsFromMatchDetailsBuilder = uniqTeamsBuilderFunc("hattrick.match_details").isLeagueMatch
    val teamsFromPlayerStatsBuilder = uniqTeamsBuilderFunc("hattrick.player_stats").isLeagueMatch
    val teamsFromTeamRankingsBuilder = uniqTeamsBuilderFunc("hattrick.team_rankings")
    val teamsFromTeamDetailsBuilder = uniqTeamsBuilderFunc("hattrick.team_details")


    val teamsFromMatchDetails = teamsFromMatchDetailsBuilder.sqlWithParameters().build
      .as(Count.mapper.*)
    val teamsFromPlayerStats = teamsFromPlayerStatsBuilder.sqlWithParameters().build
      .as(Count.mapper.*)
    val teamsFromTeamRankings = teamsFromTeamRankingsBuilder.sqlWithParameters().build
      .as(Count.mapper.*)
    val teamsFromTeamDetails = teamsFromTeamDetailsBuilder.sqlWithParameters().build
      .as(Count.mapper.*)

    if (!isNumbersEqual(teamsFromMatchDetails.size, teamsFromPlayerStats.size, teamsFromTeamRankings.size, teamsFromTeamDetails.size)) {
      throw new Exception(s"Teams number from match_details: ${teamsFromMatchDetails.size}, " +
        s"teams number from player_stats: ${teamsFromPlayerStats.size}, " +
        s"teams number from team_rankings: ${teamsFromTeamRankings.size}, " +
        s"teams number from team_details: ${teamsFromTeamDetails.size}")
    }

    teamsFromMatchDetails.sortBy(count => (count.leagueId, count.divisionLevel)).zip(teamsFromPlayerStats.sortBy(count => (count.leagueId, count.divisionLevel)))
      .zip(teamsFromTeamRankings.sortBy(count => (count.leagueId, count.divisionLevel))).zip(teamsFromTeamDetails.sortBy(count => (count.leagueId, count.divisionLevel)))
      .foreach{case (((matchDetailsCount, playerStatsCount), teamRankingsCount), teamDetailsCount) =>
        if (!isNumbersEqual(matchDetailsCount.cnt, playerStatsCount.cnt, teamRankingsCount.cnt, teamDetailsCount.cnt)) {
          throw new Exception(s"League ${matchDetailsCount.leagueId} division level ${matchDetailsCount.divisionLevel} team counts don't match: " +
            s"match_details: ${matchDetailsCount.cnt}, " +
            s"playerStats: ${playerStatsCount.cnt}, " +
            s"teamRankings: ${teamRankingsCount.cnt}, " +
            s"teamDetails: ${teamDetailsCount.cnt}")
        }
      }
  }

  private def isNumbersEqual(numbers: Long*): Boolean = {
    numbers.distinct.size == 1
  }
}
