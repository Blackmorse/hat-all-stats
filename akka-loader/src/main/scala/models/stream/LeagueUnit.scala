package models.stream

case class LeagueUnit(leagueUnitId: Int,
                      leagueUnitName: String,
                      level: Int,
                      league: League)

case class League(leagueId: Int,
                  seasonOffset: Int,
                  nextRound: Int,
                  season: Int,
                  activeTeams: Int)