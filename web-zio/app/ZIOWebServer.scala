import hattid.zio.HattidEnv
import models.web.{HattidError, toResponse}
import routes.*
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
    .handleError(_.toResponse)

  def run: ZIO[ZIOAppArgs & Scope, Throwable, Nothing] = for {
    args   <- getArgs
    envs   <- HattidEnv.env(args.head)
    t      <- Server.serve[HattidEnv](routes)
              .provideEnvironment(envs)
  } yield t
}
