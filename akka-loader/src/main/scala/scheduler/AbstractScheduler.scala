package scheduler

import chpp.worlddetails.models.WorldDetails

abstract class AbstractScheduler(val worldDetails: WorldDetails) {
  protected val threeHoursMs: Long = 1000L * 60 * 60 * 3

  def schedule(): Unit

  protected def scheduleFrom(leagueId: Int)

  def scheduleFrom(leagueName: String): Unit = scheduleFrom(findLeagueIdByName(leagueName))

  protected def load(leagueIds: Seq[Int]): Unit

  def load(leagueNames: String): Unit = {
    val leagueIds = leagueNames.split(",")
      .map(leagueName => findLeagueIdByName(leagueName))
    load(leagueIds)
  }

  private def findLeagueIdByName(leagueName: String): Int = {
   if (leagueName.forall(_.isDigit)) {
    leagueName.toInt
   } else {
     worldDetails.leagueList
      .find(_.leagueName == leagueName)
      .getOrElse(throw new IllegalArgumentException(s"Unknown country $leagueName"))
      .leagueId
   }
  }
}
