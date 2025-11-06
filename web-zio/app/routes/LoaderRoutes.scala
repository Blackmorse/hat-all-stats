package routes

import controllers.LeagueTime
import databases.requests.matchdetails.HistoryInfoRequest
import hattid.CommonData
import hattid.zio.*
import models.web.{BadRequestError, HattidError, NotFoundError}
import service.leagueinfo.{Finished, LeagueInfoServiceZIO, Loading, Scheduled}
import zio.*
import zio.http.*
import zio.json.JsonDecoder

object LoaderRoutes {
  private val PostLoader = Method.POST / "api" / "loader"

  val routes: Seq[Route[HattidEnv, HattidError]] = Seq(
    PostLoader / "leagueRound" -> leagueRoundHandler,
    PostLoader / "scheduleInfo" -> scheduleInfoHandler,
    PostLoader / "loadingStarted" -> loadingStartedHandler,
  )

  private def loadingStartedHandler = handler { (req: Request) =>
    for {
      leagueId          <- req.intParam("leagueId")
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      _                 <- leagueInfoService.setLoadingStatus(leagueId, Loading)
    } yield Response.text("")
  }

  private def scheduleInfoHandler = handler { (req: Request) =>
    for {
      leagueInfoService <- ZIO.service[LeagueInfoServiceZIO]
      body              <- req.body.asString
                            .mapError(e => BadRequestError("Unable to read body"))
      schedules         <- ZIO.fromEither(JsonDecoder[Seq[LeagueTime]].decodeJson(body))
                            .tapError(e => ZIO.logError(e))
                            .mapError(e => BadRequestError("Unable to parse schedule info"))
      _                 <- ZIO.collectAll(
                             schedules.map(leagueTime => leagueInfoService.setLoadingStatus(leagueTime.leagueId, Scheduled(leagueTime.time)))
      )
    } yield Response.text("")
  }

  private def leagueRoundHandler = handler { (req: Request) =>
    for {
      season     <- req.intParam("season")
      leagueId   <- req.intParam("leagueId")
      round      <- req.intParam("round")
      roundInfos <- historyInfoRequest(season, leagueId, round)
      // TODO - update overview cache in case it's a reload of some round?
      leagueInfoServiceZIO <- ZIO.service[LeagueInfoServiceZIO]
      _                    <- leagueInfoServiceZIO.addAnotherRound(leagueId, season, round, roundInfos)
      _                    <- leagueInfoServiceZIO.setLoadingStatus(leagueId, Finished)
      _                    <- if (leagueId == CommonData.LAST_SERIES_LEAGUE_ID) leagueInfoServiceZIO.finishAll()
      else ZIO.unit
    } yield Response.text("Ok")
  }

  private def historyInfoRequest(season: Int, leagueId: Int, round: Int) =
    HistoryInfoRequest.execute(leagueId = Some(leagueId),
      season = Some(season),
      round = Some(round))
      .flatMap(roundInfos => {
        if (roundInfos.isEmpty) {
          ZIO.fail(NotFoundError("LeagueRound", "",  s"Not found history for league $leagueId, season $season, round $round"))
        } else {
          ZIO.succeed(roundInfos)
        }
      })
}
