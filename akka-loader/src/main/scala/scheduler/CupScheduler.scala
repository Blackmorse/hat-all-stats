package scheduler

import executors.TaskExecutorActor.{ScheduleFinished, ScheduleTask}
import akka.actor.ActorRef
import chpp.worlddetails.models.WorldDetails
import hattid.{CupSchedule, ScheduleEntry}

import java.util.{Calendar, Date}

class CupScheduler(worldDetails: WorldDetails,
                   taskExecutorActor: ActorRef) extends AbstractScheduler(worldDetails) {
  private val cupSchedule = worldDetails.leagueList.map(league => ScheduleEntry(league.leagueId, league.cupMatchDate.get))

  override def schedule(): Unit = {
    val dayLightSavingOffset = if (CupSchedule.isSummerTimeNow()) 0L else 1000L * 60 * 60

    CupSchedule.normalizeCupScheduleToDayOfWeek(cupSchedule, Calendar.MONDAY)
      .filter(_.date.after(new Date()))
      .map(scheduleEntry => {
        val scheduledDate = new Date(scheduleEntry.date.getTime + threeHoursMs + dayLightSavingOffset)
        ScheduleTask(scheduleEntry.leagueId, scheduledDate)
      })
      .foreach(task => taskExecutorActor ! task)

    taskExecutorActor ! ScheduleFinished
  }

  override protected def scheduleFrom(leagueId: Int): Unit = {
    val dayLightSavingOffset = if (CupSchedule.isSummerTimeNow()) 0L else 1000L * 60 * 60

    CupSchedule.normalizeCupScheduleToDayOfWeek(cupSchedule, Calendar.MONDAY)
      .sortBy(_.date)
      .dropWhile(_.leagueId != leagueId)
      .map(scheduleEntry => {
        val scheduledDate = new Date(scheduleEntry.date.getTime + threeHoursMs + dayLightSavingOffset)
        ScheduleTask(scheduleEntry.leagueId, scheduledDate)
      })
      .foreach(task => taskExecutorActor ! task)

    taskExecutorActor ! ScheduleFinished
  }

  override protected def loadIds(leagueIds: Seq[Int]): Unit = {
    leagueIds.foreach(leagueID => taskExecutorActor ! ScheduleTask(leagueID, new Date()))
  }
}
