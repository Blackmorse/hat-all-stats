package scheduler

import executors.TaskExecutorActor.{ScheduleFinished, ScheduleTask}
import akka.actor.ActorRef
import chpp.worlddetails.models.WorldDetails
import hattid.CommonData
import scheduler.LeagueScheduler.{countriesToMinutesOffset, firstLeagueId, lastLeagueId}

import java.util.Date

object LeagueScheduler {
  private val firstLeagueId = 1000
  private val lastLeagueId = CommonData.LAST_SERIES_LEAGUE_ID

  val countriesToMinutesOffset = Map(
    24 -> 90L,  //Poland
    4 -> 30L,   //Italy
    36 -> 60L,  //Spain
    46 -> 135L, //Switzerland
    3 -> 30L    //Germany
  )
}

class LeagueScheduler(worldDetails: WorldDetails,
                     taskExecutorActor: ActorRef) extends AbstractScheduler(worldDetails) {
  override def schedule(): Unit = {
    val leagues = worldDetails.leagueList
      .filter(_.matchRound <= 14)

    val tasks = leagues
      .filter(league => league.seriesMatchDate.after(new Date()))
      .map(league => {
        val minutesOffset  = countriesToMinutesOffset.getOrElse(league.leagueId, 0L)

        val scheduledDate = new Date(league.seriesMatchDate.getTime + threeHoursMs
          + minutesOffset * 60 * 1000)
        ScheduleTask(league.leagueId, scheduledDate)
      })

    tasks.foreach(task => taskExecutorActor ! task)

    taskExecutorActor ! ScheduleFinished
  }


  override protected def loadIds(leagueIds: Seq[Int]): Unit = {
    leagueIds.foreach(leagueID => taskExecutorActor ! ScheduleTask(leagueID, new Date()))
  }


  override protected def scheduleFrom(leagueId: Int): Unit = {
    val lastLeague = worldDetails.leagueList.filter(_.leagueId == lastLeagueId).head
    val firstLeague = worldDetails.leagueList.filter(_.leagueId == firstLeagueId).head
    val matchesAlreadyFinished = firstLeague.seriesMatchDate.after(new Date()) &&
      lastLeague.seriesMatchDate.after(new Date()) && lastLeague.seriesMatchDate.after(firstLeague.seriesMatchDate)

    val previousWeekMs = if (matchesAlreadyFinished) 1000L * 3600 * 24 * 7 else 0L
    worldDetails.leagueList
      .map(league => {
        val minutesOffset: Long  = countriesToMinutesOffset.getOrElse(league.leagueId, 0)

        val date = if (league.seriesMatchDate.after(lastLeague.seriesMatchDate)) {
          new Date(league.seriesMatchDate.getTime - 1000L * 3600 * 24 * 7
            + threeHoursMs + (minutesOffset * 60 * 1000))
        } else {
          new Date(league.seriesMatchDate.getTime + threeHoursMs + + minutesOffset * 60 * 1000)
        }

        ScheduleTask(league.leagueId, new Date(date.getTime - previousWeekMs))
      })
      .sortBy(_.time)
      .dropWhile(_.leagueId != leagueId)
      .foreach(taskExecutorActor ! _)

    taskExecutorActor ! ScheduleFinished
  }
}
