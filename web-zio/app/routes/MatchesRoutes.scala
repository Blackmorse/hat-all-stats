package routes

import databases.requests.matchdetails.{AnnoySimilarMatchesRequest, SimilarMatchesRequest}
import hattid.zio.*
import models.web.matches.SingleMatch
import models.web.{BadRequestError, HattidError, HattidInternalError}
import service.{ChppService, SimilarMatchesService}
import zio.*
import zio.http.*
import zio.json.*

object MatchesRoutes {

  val routes: Seq[Route[HattidEnv, HattidError]] = Seq(
    Method.GET  / "api" / "matches" / "similarMatches" -> similarMatchesHandler,
    Method.GET  / "api" / "matches" / "similarMatchesWithAnnoy" -> similarMatchesWithAnnoyHandler,
    Method.POST / "api" / "matches" / "similarMatchesByRatings" -> similarMatchesByRatingsHandler,
    Method.POST / "api" / "matches" / "similarMatchesByRatingsWithAnnoy" -> similarMatchesByRatingsWithAnnyHandler,
    Method.GET  / "api" / "matches" / "singleMatch" -> singleMatchHandler,
  )
  
  private def singleMatchHandler = handler { (req: Request) =>
    for {
      matchId      <- req.longParam("matchId")
      chppService  <- ZIO.service[ChppService]
      matchDetails <- chppService.matchDetails(matchId)
    } yield Response.json {
      val matc = matchDetails.matc
      val homeTeam = matc.homeTeam
      val awayTeam = matc.awayTeam
      SingleMatch.fromHomeAwayTeams(
        homeTeam = homeTeam,
        awayTeam = awayTeam,
        homeGoals = Some(homeTeam.goals),
        awayGoals = Some(awayTeam.goals),
        matchId = Some(matc.matchId)).toJson
    }
  }

  private def similarMatchesByRatingsWithAnnyHandler = handler { (req: Request) => 
    for {
      singleMatch             <- singleMatch(req)
      accuracy                <- req.intParam("accuracy")
      considerTacticType      <- req.boolParam("considerTacticType")
      considerTacticSkill     <- req.boolParam("considerTacticSkill")
      considerSetPiecesLevels <- req.boolParam("considerSetPiecesLevels")
      statsOpt                <- AnnoySimilarMatchesRequest.execute(
                                  singleMatch = singleMatch,
                                  accuracy = accuracy,
                                  considerTacticType = considerTacticType,
                                  considerTacticSkill = considerTacticSkill,
                                  considerSetPiecesLevels = considerSetPiecesLevels)
    } yield Response.json(statsOpt.toJson)
  }
  
  private def similarMatchesByRatingsHandler = handler { (req: Request) =>
    for {
      singleMatch <- singleMatch(req)
      accuracy    <- req.doubleParam("accuracy")
      statsOpt    <- SimilarMatchesRequest.execute(singleMatch, accuracy)
    } yield Response.json(statsOpt.toJson)
  }
  
  private def singleMatch(req: Request): ZIO[Any, HattidError, SingleMatch] =
    for {
      body        <- req.body.asString
        .mapError(_ => HattidInternalError("Failed to read request body"))
      singleMatch <- ZIO.fromEither(JsonDecoder[SingleMatch].decodeJson(body))
        .mapError(_ => BadRequestError("Invalid Request Body"))
    } yield singleMatch

  private def similarMatchesHandler = handler { (req: Request) =>
    for {
      matchId               <- req.longParam("matchId")
      accuracy              <- req.doubleParam("accuracy")
      similarMatchesService <- ZIO.service[SimilarMatchesService]
      matches               <- similarMatchesService.similarMatchesStats(matchId, accuracy)
    } yield Response.json(matches.toJson)
  }

  private def similarMatchesWithAnnoyHandler = handler { (req: Request) =>
    for {
      matchId <- req.longParam("matchId")
      accuracy <- req.intParam("accuracy")
      considerTacticType <- req.boolParam("considerTacticType")
      considerTacticSkill <- req.boolParam("considerTacticSkill")
      considerSetPiecesLevels <- req.boolParam("considerSetPiecesLevels")
      similarMatchesService <- ZIO.service[SimilarMatchesService]
      result <- similarMatchesService.similarMatchesAnnoyStats(matchId, accuracy, considerTacticType, considerTacticSkill, considerSetPiecesLevels)
    } yield Response.json(result.toJson)
  }
}
