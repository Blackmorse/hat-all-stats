package tests

import chpp.worlddetails.models.WorldDetails
import sqlbuilder.Select
import sqlbuilder.functions.uniqExact
import tests.models.Count

import java.sql.DriverManager

object ClickhouseTests {

  def testNoHolesInLeagueRounds(worldDetails: WorldDetails)(implicit connection: java.sql.Connection): Seq[String] = {
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
    results.groupBy(_.leagueId).flatMap{ case (leagueId, leagueCounts) =>
      leagueCounts.groupBy(_.divisionLevel).flatMap{case (divisionLevel, divisionLevelCounts) =>
        val inconsistensyIssue = if(!(1 to round).forall(r => divisionLevelCounts.exists(_.round == r))) {
          Some(s"Inconsistency for leagueId $leagueId, divisionLevel $divisionLevel" +
            s"(at least one divisionLevel-round is missing): ${divisionLevelCounts.sortBy(_.round)}")
        } else {
          None
        }
        val countsIssues = for (r <- 2 to round) yield {
          val previousRoundCount = divisionLevelCounts.find(_.round == r - 1).get.cnt
          val currentRoundCount = divisionLevelCounts.find(_.round == r).get.cnt

          if(Math.abs(previousRoundCount - currentRoundCount) > currentRoundCount * 0.3 && currentRoundCount > 12) {
            Some(s"Suspicious teams number difference for rounds ${r - 1} and $r: ${divisionLevelCounts.sortBy(_.round)}")
          } else {
            None
          }
        }

        (Seq(inconsistensyIssue) ++ countsIssues).flatten
      }
    }.toSeq
  }

  def testNumberOfTeamRankingsRecords(worldDetails: WorldDetails)(implicit connection: java.sql.Connection): Seq[String] = {
    import sqlbuilder.SqlBuilder.implicits._

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

    val countsIssue = if (!isNumbersEqual(matchDetailsCounts.size, teamRankingsCounts.size)) {
      Some(s"Counts doesn't match while test of team rankings consistency: " +
        s"matchDetails: ${matchDetailsCounts.size}, teamRankings: ${teamRankingsCounts.size}")
    } else {
      None
    }

    val mismatchIssues = matchDetailsCounts.sortBy(count => (count.leagueId, count.divisionLevel)).zip(teamRankingsCounts.sortBy(count => (count.leagueId, count.divisionLevel)))
      .map{ case (matchDetailsCount, teamRankingsCount) =>
        if (matchDetailsCount.cnt * 2 != teamRankingsCount.cnt) {
          Some(s"MatchDetails and TeamRankings for ${matchDetailsCount.leagueId} division level ${matchDetailsCount.divisionLevel} doesn't match: " +
          s"match details: ${matchDetailsCount.cnt}, " +
          s"team rankings: ${teamRankingsCount.cnt}")
        } else {
          None
        }
      }
    (Seq(countsIssue) ++ mismatchIssues).flatten
  }

  def testTeamCounts(worldDetails: WorldDetails)(implicit connection: java.sql.Connection): Seq[String] = {

    import sqlbuilder.SqlBuilder.implicits._

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

    val teamNumbersIssue = if (!isNumbersEqual(teamsFromMatchDetails.size, teamsFromPlayerStats.size, teamsFromTeamRankings.size, teamsFromTeamDetails.size)) {
      Some(s"Teams number from match_details: ${teamsFromMatchDetails.size}, " +
        s"teams number from player_stats: ${teamsFromPlayerStats.size}, " +
        s"teams number from team_rankings: ${teamsFromTeamRankings.size}, " +
        s"teams number from team_details: ${teamsFromTeamDetails.size}")
    } else {
      None
    }

    val teamCountsIssues = teamsFromMatchDetails.sortBy(count => (count.leagueId, count.divisionLevel)).zip(teamsFromPlayerStats.sortBy(count => (count.leagueId, count.divisionLevel)))
      .zip(teamsFromTeamRankings.sortBy(count => (count.leagueId, count.divisionLevel))).zip(teamsFromTeamDetails.sortBy(count => (count.leagueId, count.divisionLevel)))
      .map{case (((matchDetailsCount, playerStatsCount), teamRankingsCount), teamDetailsCount) =>
        if (!isNumbersEqual(matchDetailsCount.cnt, playerStatsCount.cnt, teamRankingsCount.cnt, teamDetailsCount.cnt)) {
          Some(s"League ${matchDetailsCount.leagueId} division level ${matchDetailsCount.divisionLevel} team counts don't match: " +
            s"match_details: ${matchDetailsCount.cnt}, " +
            s"playerStats: ${playerStatsCount.cnt}, " +
            s"teamRankings: ${teamRankingsCount.cnt}, " +
            s"teamDetails: ${teamDetailsCount.cnt}")
        } else {
          None
        }
      }

    (Seq(teamNumbersIssue) ++ teamCountsIssues).flatten
  }

  private def isNumbersEqual(numbers: Long*): Boolean = {
    numbers.distinct.size == 1
  }
}
