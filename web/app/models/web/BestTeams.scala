package models.web

import models.clickhouse.TeamRating
import service.DefaultService

case class BestTeams(teams: List[TeamRating], page: Int, prevUrl: Option[String], nextUrl: Option[String])

object BestTeams{
  def apply(teams: List[TeamRating], page: Int, pageUrlFunc: Int => String): BestTeams = {
    val prevUrl = if(page <= 0) None else Some(pageUrlFunc(page - 1))
    val nextUrl = if (teams.size < DefaultService.PAGE_SIZE) None else Some(pageUrlFunc(page + 1))
    BestTeams(teams, page, prevUrl, nextUrl)
  }
}
