package controllers

import chpp.matchdetails.MatchDetailsRequest
import chpp.matchdetails.models.MatchDetails
import databases.requests.model.`match`.MatchRatings
import models.clickhouse.TeamMatchInfo
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import service.SimilarMatchesService
import webclients.ChppClient

import java.util.Date
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global

case class WebTeamMatch(teamMatchInfo: TeamMatchInfo, date: Date,
                        homeTeamId: Long, homeTeamName: String, homeTeamGoals: Int,
                        awayTeamId: Long, awayTeamName: String, awayTeamGoals: Int)

case class SingleMatch(homeTeamName: String,
                       homeTeamId: Long,
                       homeGoals: Int,
                       awayTeamName: String,
                       awayTeamId: Long,
                       awayGoals: Int,
                       matchId: Long,
                       homeMatchRatings: MatchRatings,
                       awayMatchRatings: MatchRatings)

object SingleMatch {
  implicit val writes: OWrites[SingleMatch] = Json.writes[SingleMatch]
}

@Singleton
class MatchController @Inject()(val controllerComponents: ControllerComponents,
                                val similarMatchesService: SimilarMatchesService,
                                val chppClient: ChppClient) extends BaseController {

  def similarMatches(matchId: Long, accuracy: Double): Action[AnyContent] = Action.async { implicit request =>
    similarMatchesService.similarMatchesStats(matchId, accuracy)
      .map(similarMatchesStats =>
        similarMatchesStats.map(s => Ok(Json.toJson(s))).getOrElse(Ok("")))
  }

  def singleMatch(matchId: Long): Action[AnyContent] = Action.async { implicit request =>
    chppClient.execute[MatchDetails, MatchDetailsRequest](MatchDetailsRequest(matchId = Some(matchId)))
      .map(matchDetails => {
        val matc = matchDetails.matc
        val homeTeam = matc.homeTeam
        val awayTeam = matc.awayTeam
        val singleMatch = SingleMatch(homeTeamName = homeTeam.teamName,
          homeTeamId = homeTeam.teamId,
          homeGoals = homeTeam.goals,
          awayTeamName = awayTeam.teamName,
          awayTeamId = awayTeam.teamId,
          awayGoals = awayTeam.goals,
          matchId = matc.matchId,
          homeMatchRatings = MatchRatings(
            formation = homeTeam.formation,
            tacticType = homeTeam.tacticType,
            tacticSkill = homeTeam.tacticSkill,
            ratingMidfield = homeTeam.ratingMidfield,
            ratingRightDef = homeTeam.ratingRightDef,
            ratingMidDef = homeTeam.ratingMidDef,
            ratingLeftDef = homeTeam.ratingLeftDef,
            ratingRightAtt = homeTeam.ratingRightAtt,
            ratingMidAtt = homeTeam.ratingMidAtt,
            ratingLeftAtt = homeTeam.ratingLeftAtt
          ),
          awayMatchRatings = MatchRatings(
            formation = awayTeam.formation,
            tacticType = awayTeam.tacticType,
            tacticSkill = awayTeam.tacticSkill,
            ratingMidfield = awayTeam.ratingMidfield,
            ratingRightDef = awayTeam.ratingRightDef,
            ratingMidDef = awayTeam.ratingMidDef,
            ratingLeftDef = awayTeam.ratingLeftDef,
            ratingRightAtt = awayTeam.ratingRightAtt,
            ratingMidAtt = awayTeam.ratingMidAtt,
            ratingLeftAtt = awayTeam.ratingLeftAtt
          ))
        Ok(Json.toJson(singleMatch))
      })
  }
}

