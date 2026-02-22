package com.blackmorse.hattid.web

import com.blackmorse.hattid.web.routes.*
import com.blackmorse.hattid.web.zios.*
import com.blackmorse.hattid.web.models.web.HattidError
import zio.*
import zio.http.*

object ZIOWebServer extends ZIOAppDefault {

  private val routesSeq: Seq[Route[HattidEnv, HattidError]] =
    WorldRoutes.routes ++
      CommonRoutes.routes ++
      AnalyzerRoutes.routes ++
      DivisionLevelRoutes.routes ++
      LeagueRoutes.routes ++
      LeagueUnitRoutes.routes ++
      LoaderRoutes.routes ++
      MatchesRoutes.routes ++
      OverviewRoutes.routes ++
      PlayerRoutes.routes ++
      TeamRoutes.routes

  val routes: Routes[HattidEnv, Nothing] = Routes(routesSeq.head, routesSeq.tail *)
    .tapErrorZIO(e => ZIO.logError(e.toString))
    .handleError(_.toResponse)
//  val routes: Routes[Any, Nothing] = Routes(
//    Method.GET / "api" -> handler{
//      ZIO.succeed(Response.text("Res"))
//    }
//  )

  def run = for {
    args   <- getArgs
    envs   <- HattidEnv.env(args.head)
    t      <- Server.serve(routes)
//      .provide(Server.defaultWithPort(9000))
      .provideEnvironment(envs)
  } yield t
}
