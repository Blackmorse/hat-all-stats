package clickhouse

import chpp.commonmodels.MatchType
import chpp.worlddetails.models.League
import utils.realRound

object PlayerStatsJoiner {
  def playerStatsJoinRequest(league: League, matchType: MatchType.Value, databaseName: String): String = {
    val leagueId = league.leagueId
    val round = realRound(matchType, league)
    val season = league.season - league.seasonOffset

    s"""INSERT INTO $databaseName.player_stats SELECT
       |player_info.season,
       |player_info.league_id,
       |player_info.division_level,
       |player_info.league_unit_id,
       |player_info.league_unit_name,
       |player_info.team_id,
       |player_info.team_name,
       |player_info.time,
       |player_info.dt,
       |player_info.round,
       |player_info.cup_level,
       |player_info.cup_level_index,
       |player_info.match_id,
       |player_info.player_id,
       |player_info.first_name,
       |player_info.last_name,
       |player_info.age,
       |player_info.days,
       |player_info.role_id,
       |player_info.played_minutes,
       |player_info.rating,
       |player_info.rating_end_of_match,
       |player_info.injury_level,
       |player_info.tsi,
       |player_info.salary,
       |player_events.yellow_cards,
       |player_events.red_cards,
       |player_events.goals,
       |player_info.nationality,
       |player_info.match_duration
       |FROM $databaseName.player_info
       |LEFT JOIN
       |(
       |SELECT *
       |FROM $databaseName.player_events
       |WHERE (season = $season) AND (round = $round)
       |)
       |AS player_events ON (player_info.player_id = player_events.player_id) AND (player_info.season = player_events.season) AND (player_info.round = player_events.round)
       |WHERE (season = $season) AND (league_id = $leagueId) AND (round = $round)""".stripMargin
  }
}
