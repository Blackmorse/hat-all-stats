package com.blackmorse.hattid.web.service.cache

import com.blackmorse.hattid.web.databases.requests.model.overview.{AveragesOverview, NumberOverview}
import com.blackmorse.hattid.web.databases.requests.overview.*
import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import com.blackmorse.hattid.web.models.web.HattidError
import com.blackmorse.hattid.web.service.cache.CacheKey.EntityType
import zio.cache.{Cache, Lookup}
import zio.{ZIO, ZLayer}

import scala.concurrent.duration.*

object CacheKey {
  type EntityType = 
      "numberOverview" 
    | "formations" 
    | "averageOverview" 
    | "surprisingMatches" 
    | "topHatstatsTeams" 
    | "topSalaryTeams" 
    | "topMatches" 
    | "topSalaryPlayers" 
    | "topRatingPlayers" 
    | "topMatchAttendance"
    | "topTeamVictories" 
    | "topSeasonScorers"
}

case class CacheKey(
                     entityType: EntityType,
                     season: Int,
                     round: Int,
                     leagueId: Option[Int],
                     divisionLevel: Option[Int],
                   )

object OverviewCache {
  type CacheType = Cache[CacheKey, HattidError, List[Product]]
  
  val layer: ZLayer[ClickhousePool, Nothing, CacheType] = ZLayer.fromZIO {
    Cache.make(
      capacity = 50000,
      timeToLive = zio.Duration.fromScala(28.days),
      lookup = Lookup({ (key: CacheKey) =>
        key.entityType match {
          case "numberOverview" =>
            for {
              numbers <- NumberOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel).map(_.head)
              _ <- ZIO.debug("Called! " + key)
              newTeams <- NewTeamsOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel).map(_.head)
            } yield NumberOverview(numbers, newTeams) :: Nil
          case "formations" =>
            FormationsOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          case "averageOverview" =>
            for {
              matchAverages <- OverviewMatchAveragesRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
              teamPlayerAverages <- OverviewTeamPlayerAveragesRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
            } yield AveragesOverview(matchAverages.head, teamPlayerAverages.head) :: Nil
          case "surprisingMatches" =>
            SurprisingMatchesOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          case "topHatstatsTeams" =>
            TopHatstatsTeamOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          case "topSalaryTeams" =>
            TopSalaryTeamOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          case "topMatches" =>
            TopMatchesOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          case "topSalaryPlayers" =>
            TopSalaryPlayerOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          case "topRatingPlayers" =>
            TopRatingPlayerOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          case "topMatchAttendance" =>
            TopAttendanceMatchesOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          case "topTeamVictories" =>
            TopVictoriesTeamsOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
          case "topSeasonScorers" =>
            TopSeasonScorersOverviewRequest.executeZio(key.season, key.round, key.leagueId, key.divisionLevel)
        }
      })
    )
  }
}
