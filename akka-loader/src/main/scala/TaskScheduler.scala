import TaskScheduler.countriesToMinutesOffset
import actors.LeagueTaskExecutorActor.ScheduleTask
import akka.actor.{ActorRef, ActorSystem}
import alltid.AlltidClient
import chpp.matchesarchive.models.MatchType
import chpp.worlddetails.models.{League, WorldDetails}

import java.util.Date

object TaskScheduler {
  val countriesToMinutesOffset = Map(
    24 -> 90L,  //Poland
    4 -> 30L,   //Italy
    36 -> 60L,  //Spain
    46 -> 135L, //Switzerland
    3 -> 30L    //Germany
  )
}

class TaskScheduler(worldDetails: WorldDetails,
                    taskExecutorActor: ActorRef,
                    matchType: MatchType.Value)(implicit actorSystem: ActorSystem) {
  private val firstLeagueId = 1000
  private val lastLeagueId = 100
  private val threeHoursMs = 1000L * 60 * 60 * 3

  def schedule(dateTimeFunc: League => Date): Unit = {
    val value = worldDetails.leagueList
      .filter(_.matchRound <= 14)
    val tasks = value
      .map(league => {
        val minutesOffset  = if(matchType == MatchType.LEAGUE_MATCH)
          countriesToMinutesOffset.getOrElse(league.leagueId, 0L)
        else 0L

        val scheduledDate = new Date(dateTimeFunc(league).getTime + threeHoursMs
          + minutesOffset * 60 * 1000)
        ScheduleTask(league.leagueId, scheduledDate)
      })

    tasks.foreach(task => taskExecutorActor ! task)

    if (tasks.nonEmpty && matchType == MatchType.LEAGUE_MATCH) {
      AlltidClient.notifyScheduleInfo(tasks)
    }
  }

  def load(leagueNames: String): Unit = {
    val leagueIds = leagueNames.split(",")
      .map(leagueName => worldDetails.leagueList.find(_.leagueName == leagueName).getOrElse(throw new IllegalArgumentException(s"Unknown county $leagueName"))
      .leagueId)
    load(leagueIds)
  }

  def load(leagueIds: Seq[Int]): Unit = {
    leagueIds.foreach(leagueID => taskExecutorActor ! ScheduleTask(leagueID, new Date()))
  }

  def scheduleFrom(leagueName: String, dateTimeFunc: League => Date): Unit = {
    val leagueId = worldDetails.leagueList
      .find(_.leagueName == leagueName)
      .getOrElse(throw new IllegalArgumentException(s"Unknown county $leagueName"))
      .leagueId

    scheduleFrom(leagueId, dateTimeFunc)
  }

  def scheduleFrom(leagueId: Int, dateTimeFunc: League => Date): Unit = {
    val lastLeague = worldDetails.leagueList.filter(_.leagueId == lastLeagueId).head
    val firstLeague = worldDetails.leagueList.filter(_.leagueId == firstLeagueId).head
    val matchesAlreadyFinished = dateTimeFunc(firstLeague).after(new Date()) &&
      dateTimeFunc(lastLeague).after(new Date()) && dateTimeFunc(lastLeague).after(dateTimeFunc(firstLeague))

    val previousWeekMs = if (matchesAlreadyFinished) 1000L * 3600 * 24 * 7 else 0L

    worldDetails.leagueList
      .map(league => {
        val minutesOffset: Long  = countriesToMinutesOffset.getOrElse(league.leagueId, 0)

        val date = if (dateTimeFunc(league).after(dateTimeFunc(lastLeague))) {
          new Date(dateTimeFunc(league).getTime - 1000L * 3600 * 24 * 7
           + threeHoursMs + (minutesOffset * 60 * 1000))
        } else {
          new Date(dateTimeFunc(league).getTime + threeHoursMs + + minutesOffset * 60 * 1000)
        }

        ScheduleTask(league.leagueId, new Date(date.getTime - previousWeekMs))
      })
      .sortBy(_.time)
      .dropWhile(_.leagueId != leagueId)
      .foreach(taskExecutorActor ! _)
  }
}
