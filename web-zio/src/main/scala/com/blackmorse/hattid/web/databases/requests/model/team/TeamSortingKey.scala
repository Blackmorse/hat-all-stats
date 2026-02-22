package com.blackmorse.hattid.web.databases.requests.model.team

case class TeamSortingKey(teamId: Long, teamName: String, leagueUnitId: Long,
                          leagueUnitName: String, leagueId: Int)

object TeamSortingKey {
  implicit val jsonEncoder: zio.json.JsonEncoder[TeamSortingKey] = zio.json.DeriveJsonEncoder.gen[TeamSortingKey]
}