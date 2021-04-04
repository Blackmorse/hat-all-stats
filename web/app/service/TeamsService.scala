package service

import databases.dao.RestClickhouseDAO
import databases.requests.model.team.CreatedSameTimeTeam
import databases.requests.teamdetails.TeamsCreatedSameTimeRequest
import play.api.libs.json.Json
import play.api.mvc.QueryStringBindable
import service.leagueinfo.LeagueInfoService

import java.util.Date
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class CreatedSameTimeTeamExtended(season: Int,
                                       round: Int,
                                       createdSameTimeTeam: CreatedSameTimeTeam)

object CreatedSameTimeTeamExtended {
  implicit val writes = Json.writes[CreatedSameTimeTeamExtended]
}


trait HattrickPeriod

case object Round extends HattrickPeriod
case object Season extends HattrickPeriod

object HattrickPeriod {
  implicit def queryStringBindable(implicit stringBuilder: QueryStringBindable[String]) = new QueryStringBindable[HattrickPeriod] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, HattrickPeriod]] = {
      stringBuilder.bind("period", params)
        .map(periodEither => periodEither.flatMap(period =>{
          if (period == "round") {
            Right(Round)
          } else if (period == "season") {
            Right(Season)
          } else {
            Left("Unknown period type. Available values: season, round")
          }
        }))
    }

    override def unbind(key: String, value: HattrickPeriod): String = {
      val valueStr = value match {
        case Season => "season"
        case Round => "round"
      }
      stringBuilder.unbind("period", valueStr)
    }
  }
}

class TeamsService @Inject()(leagueInfoService: LeagueInfoService,
                             seasonsService: SeasonsService,
                             implicit val restClickhouseDAO: RestClickhouseDAO) {

  def teamsCreatedSamePeriod(period: HattrickPeriod, foundedDate: Date,
                             leagueId: Int): Future[List[CreatedSameTimeTeamExtended]] = {
    val round = leagueInfoService.leagueInfo(leagueId).currentRound()
    val ranges = seasonsService.getSeasonAndRoundRanges(foundedDate)

    val range = period match {
      case Round => ranges.roundRange
      case Season => ranges.seasonRange
    }

    TeamsCreatedSameTimeRequest.execute(leagueId, round, range)
      .map(list => list.map(cstt => {
        val sameTeamRanges = seasonsService.getSeasonAndRoundRanges(cstt.foundedDate)
        CreatedSameTimeTeamExtended(season = ranges.season,
          round = sameTeamRanges.round,
          createdSameTimeTeam = cstt)
      }))
  }
}
