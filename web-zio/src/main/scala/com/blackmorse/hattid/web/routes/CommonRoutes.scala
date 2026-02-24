package com.blackmorse.hattid.web.routes

import chpp.search.SearchRequest
import chpp.search.models.SearchType
import com.blackmorse.hattid.web.zios.*
import hattid.CommonData
import com.blackmorse.hattid.web.models.web.teams.TeamSearchResult
import com.blackmorse.hattid.web.models.web.{HattidError, NotFoundError}
import com.blackmorse.hattid.web.service.ChppService
import com.blackmorse.hattid.web.utils.{LeagueNameParser, Romans}
import zio.ZIO
import zio.http.*
import zio.json.*

case class LongWrapper(id: Long)

object LongWrapper {
  implicit val encoder: JsonEncoder[LongWrapper] = DeriveJsonEncoder.gen[LongWrapper]
}

object CommonRoutes {
  val routes: Seq[Route[CHPPServices, HattidError]] = Seq(
    Method.GET / "api" / "health" -> handler(Response.text("")),
    Method.GET / "api" / "teamSearchByName" -> teamSearchByNameHandler,
    Method.GET / "api" / "teamSearchById" -> teamSearchByIdHandler,
    Method.GET / "api" / "league" / int("leagueId") / "leagueUnitName" / string("leagueUnitName") -> leagueUnitByNameHandler,
  )

  private def teamSearchByNameHandler = handler { (req: Request) =>
    for {
      teamName    <- req.stringParam("name")
      chppService <- ZIO.service[ChppService]
      results     <- chppService.search(SearchRequest(
                      searchType = Some(SearchType.TEAMS),
                      searchString = Some(teamName)))
    } yield Response.json(results.searchResults
      .map(result => TeamSearchResult(result.resultId, result.resultName)).toJson)
  }
  
  private def teamSearchByIdHandler = handler { (req: Request) => 
    for {
      id <- req.intParam("id")
      chppService <- ZIO.service[ChppService]
      result <- chppService.getTeamsSimple(id)
    } yield Response.json(
      result.teams
        .filter(_.teamId == id)
        .map(team => TeamSearchResult(team.teamId, team.teamName))
        .toJson
    )
  }

  private def leagueUnitByNameHandler = handler { (leagueId: Int, leagueUnitName: String, req: Request) => 
    for {
      id <- findLeagueUnitIdByName(leagueUnitName, leagueId)
    } yield Response.json(LongWrapper(id).toJson)
  }

  private def findLeagueUnitIdByName(leagueUnitName: String, leagueId: Int): ZIO[CHPPServices, HattidError, Long] = {
    if (leagueUnitName == "I.1") {
      ZIO.succeed(CommonData.higherLeagueMap(leagueId).leagueUnitId): ZIO[Any, HattidError, Long]
    } else {
      val searchRequest = if (leagueId == 1) { //Sweden
        val (division, number) = LeagueNameParser.getLeagueUnitNumberByName(leagueUnitName)
        val actualDivision = Romans(Romans(division) - 1)
        val actualNumber = if (division == "II" || division == "III") {
          ('a' + number - 1).toChar.toString
        } else {
          "." + number.toString
        }
        SearchRequest(
          searchType = Some(SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(actualDivision + actualNumber)
        )
      } else {
        SearchRequest(
          searchType = Some(SearchType.SERIES),
          searchLeagueId = Some(leagueId),
          searchString = Some(leagueUnitName)
        )
      }

      for {
        chppService <- ZIO.service[ChppService]
        searchResult <- chppService.search(searchRequest)
        leagueUnitId <- ZIO.fromOption(searchResult.searchResults.headOption.map(_.resultId))
          .mapError(_ => NotFoundError(
            entityType = NotFoundError.LEAGUE_UNIT,
            description = s"No $leagueUnitName in league $leagueId",
            entityId = s"$leagueUnitName"
          ))
      } yield leagueUnitId
    }
  }
}
