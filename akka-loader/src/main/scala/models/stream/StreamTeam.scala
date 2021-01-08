package models.stream

case class StreamTeam(leagueUnit: LeagueUnit,
                      id: Int,
                      name: String,
                      position: Int,
                      points: Int,
                      diff: Int,
                      scored: Int)
