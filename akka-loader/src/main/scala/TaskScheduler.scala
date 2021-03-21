import TaskScheduler.countriesToMinutesOffset
import actors.TaskExecutorActor.ScheduleTask
import akka.actor.ActorRef
import chpp.worlddetails.models.WorldDetails

import java.util.Date

object TaskScheduler {
  val countriesToMinutesOffset = Map(
    24 -> 90,  //Poland
    4 -> 30,   //Italy
    36 -> 60,  //Spain
    46 -> 135, //Switzerland
    3 -> 30    //Germany
  )
}

class TaskScheduler(worldDetails: WorldDetails,
                    taskExecutorActor: ActorRef) {

  def schedule(): Unit = {
    worldDetails.leagueList
      .foreach(league => {
        val minutesOffset  = countriesToMinutesOffset.getOrElse(league.leagueId, 0)
        val scheduledDate = new Date(league.seriesMatchDate.getTime +
          1000 * 60 * 60 * 3 + minutesOffset * 60 * 1000)
        taskExecutorActor ! ScheduleTask(league.leagueId, scheduledDate)
      })
  }
}
