package hattid

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

case class ScheduleEntry(leagueId: Int, date: Date)

object CupSchedule {
  private val format = new SimpleDateFormat("MMM dd HH:mm:ss zzz yyyy")

  lazy val seq = Seq(
    ScheduleEntry(1, format.parse("Jul 28 20:00:00 MSK 2021")),
    ScheduleEntry(2, format.parse("Jul 20 22:00:00 MSK 2021")),
    ScheduleEntry(3, format.parse("Aug 03 19:00:00 MSK 2021")),
    ScheduleEntry(4, format.parse("Aug 03 20:00:00 MSK 2021")),
    ScheduleEntry(5, format.parse("Aug 04 16:00:00 MSK 2021")),
    ScheduleEntry(6, format.parse("Jul 15 03:00:00 MSK 2021")),
    ScheduleEntry(7, format.parse("Jul 29 02:30:00 MSK 2021")),
    ScheduleEntry(8, format.parse("Jul 22 02:00:00 MSK 2021")),
    ScheduleEntry(9, format.parse("Jul 21 17:00:00 MSK 2021")),
    ScheduleEntry(11, format.parse("Jul 28 17:30:00 MSK 2021")),
    ScheduleEntry(12, format.parse("Jul 27 21:00:00 MSK 2021")),
    ScheduleEntry(14, format.parse("Aug 04 18:00:00 MSK 2021")),
    ScheduleEntry(15, format.parse("Jul 14 05:00:00 MSK 2021")),
    ScheduleEntry(16, format.parse("Jul 29 01:00:00 MSK 2021")),
    ScheduleEntry(17, format.parse("Jul 08 00:00:00 MSK 2021")),
    ScheduleEntry(18, format.parse("Jul 28 23:40:00 MSK 2021")),
    ScheduleEntry(19, format.parse("Jul 15 01:50:00 MSK 2021")),
    ScheduleEntry(20, format.parse("Jun 23 07:00:00 MSK 2021")),
    ScheduleEntry(21, format.parse("Jun 30 21:15:00 MSK 2021")),
    ScheduleEntry(22, format.parse("Jun 30 03:30:00 MSK 2021")),
    ScheduleEntry(23, format.parse("Jul 15 03:15:00 MSK 2021")),
    ScheduleEntry(24, format.parse("Aug 04 19:00:00 MSK 2021")),
    ScheduleEntry(25, format.parse("Jul 28 22:45:00 MSK 2021")),
    ScheduleEntry(26, format.parse("Jul 07 12:30:00 MSK 2021")),
    ScheduleEntry(27, format.parse("Jun 23 22:30:00 MSK 2021")),
    ScheduleEntry(28, format.parse("Jul 15 01:30:00 MSK 2021")),
    ScheduleEntry(29, format.parse("Jul 08 00:30:00 MSK 2021")),
    ScheduleEntry(30, format.parse("Jun 23 03:00:00 MSK 2021")),
    ScheduleEntry(31, format.parse("Jun 23 06:30:00 MSK 2021")),
    ScheduleEntry(32, format.parse("Jul 28 09:00:00 MSK 2021")),
    ScheduleEntry(33, format.parse("Jun 15 19:50:00 MSK 2021")),
    ScheduleEntry(34, format.parse("Jul 14 08:00:00 MSK 2021")),
    ScheduleEntry(35, format.parse("Jul 14 09:30:00 MSK 2021")),
    ScheduleEntry(36, format.parse("Aug 04 13:00:00 MSK 2021")),
    ScheduleEntry(37, format.parse("Jul 28 11:00:00 MSK 2021")),
    ScheduleEntry(38, format.parse("Jun 30 21:45:00 MSK 2021")),
    ScheduleEntry(39, format.parse("Jul 21 10:30:00 MSK 2021")),
    ScheduleEntry(44, format.parse("Jul 28 14:00:00 MSK 2021")),
    ScheduleEntry(45, format.parse("Jun 23 06:00:00 MSK 2021")),
    ScheduleEntry(46, format.parse("Aug 04 11:30:00 MSK 2021")),
    ScheduleEntry(47, format.parse("Jun 23 05:30:00 MSK 2021")),
    ScheduleEntry(50, format.parse("Jul 14 10:45:00 MSK 2021")),
    ScheduleEntry(51, format.parse("Jul 28 10:45:00 MSK 2021")),
    ScheduleEntry(52, format.parse("Jul 28 15:00:00 MSK 2021")),
    ScheduleEntry(53, format.parse("Jul 14 14:45:00 MSK 2021")),
    ScheduleEntry(54, format.parse("Jun 30 08:45:00 MSK 2021")),
    ScheduleEntry(55, format.parse("Jun 16 04:30:00 MSK 2021")),
    ScheduleEntry(56, format.parse("Jul 07 13:15:00 MSK 2021")),
    ScheduleEntry(57, format.parse("Jul 14 13:45:00 MSK 2021")),
    ScheduleEntry(58, format.parse("Jul 14 09:45:00 MSK 2021")),
    ScheduleEntry(59, format.parse("Jun 23 12:00:00 MSK 2021")),
    ScheduleEntry(60, format.parse("Jun 23 04:00:00 MSK 2021")),
    ScheduleEntry(61, format.parse("Jun 22 22:30:00 MSK 2021")),
    ScheduleEntry(62, format.parse("Jul 13 22:15:00 MSK 2021")),
    ScheduleEntry(63, format.parse("Jul 20 21:30:00 MSK 2021")),
    ScheduleEntry(64, format.parse("Jul 13 20:30:00 MSK 2021")),
    ScheduleEntry(66, format.parse("Jul 14 20:20:00 MSK 2021")),
    ScheduleEntry(67, format.parse("Jul 13 20:25:00 MSK 2021")),
    ScheduleEntry(68, format.parse("Jul 07 09:15:00 MSK 2021")),
    ScheduleEntry(69, format.parse("Jul 07 15:15:00 MSK 2021")),
    ScheduleEntry(70, format.parse("Jun 23 06:15:00 MSK 2021")),
    ScheduleEntry(71, format.parse("Jun 16 07:15:00 MSK 2021")),
    ScheduleEntry(72, format.parse("Jun 30 00:15:00 MSK 2021")),
    ScheduleEntry(73, format.parse("Jun 23 00:45:00 MSK 2021")),
    ScheduleEntry(74, format.parse("Jun 29 23:45:00 MSK 2021")),
    ScheduleEntry(75, format.parse("Jun 23 12:45:00 MSK 2021")),
    ScheduleEntry(76, format.parse("Jun 23 17:45:00 MSK 2021")),
    ScheduleEntry(77, format.parse("Jun 16 21:50:00 MSK 2021")),
    ScheduleEntry(79, format.parse("Jun 16 17:20:00 MSK 2021")),
    ScheduleEntry(80, format.parse("Jun 16 17:45:00 MSK 2021")),
    ScheduleEntry(81, format.parse("Jun 24 00:45:00 MSK 2021")),
    ScheduleEntry(83, format.parse("Jun 16 19:45:00 MSK 2021")),
    ScheduleEntry(84, format.parse("Jun 30 15:45:00 MSK 2021")),
    ScheduleEntry(85, format.parse("Jul 14 16:45:00 MSK 2021")),
    ScheduleEntry(88, format.parse("Jun 17 00:15:00 MSK 2021")),
    ScheduleEntry(89, format.parse("Jun 22 21:15:00 MSK 2021")),
    ScheduleEntry(91, format.parse("Jun 23 10:15:00 MSK 2021")),
    ScheduleEntry(93, format.parse("Jun 22 23:15:00 MSK 2021")),
    ScheduleEntry(94, format.parse("Jun 23 23:15:00 MSK 2021")),
    ScheduleEntry(95, format.parse("Jun 23 12:15:00 MSK 2021")),
    ScheduleEntry(96, format.parse("Jun 24 01:15:00 MSK 2021")),
    ScheduleEntry(97, format.parse("Jun 23 11:15:00 MSK 2021")),
    ScheduleEntry(98, format.parse("Jun 22 21:45:00 MSK 2021")),
    ScheduleEntry(99, format.parse("Jun 17 02:15:00 MSK 2021")),
    ScheduleEntry(100, format.parse("Jun 17 02:45:00 MSK 2021")),
    ScheduleEntry(101, format.parse("Jun 23 18:15:00 MSK 2021")),
    ScheduleEntry(102, format.parse("Jun 16 08:30:00 MSK 2021")),
    ScheduleEntry(103, format.parse("Jun 23 18:45:00 MSK 2021")),
    ScheduleEntry(104, format.parse("Jun 30 15:20:00 MSK 2021")),
    ScheduleEntry(105, format.parse("Jun 23 16:15:00 MSK 2021")),
    ScheduleEntry(106, format.parse("Jun 16 19:40:00 MSK 2021")),
    ScheduleEntry(107, format.parse("Jun 24 02:10:00 MSK 2021")),
    ScheduleEntry(110, format.parse("Jun 16 23:20:00 MSK 2021")),
    ScheduleEntry(111, format.parse("Jun 17 00:40:00 MSK 2021")),
    ScheduleEntry(112, format.parse("Jun 16 13:10:00 MSK 2021")),
    ScheduleEntry(113, format.parse("Jun 17 00:10:00 MSK 2021")),
    ScheduleEntry(117, format.parse("Jun 22 20:50:00 MSK 2021")),
    ScheduleEntry(118, format.parse("Jun 16 21:10:00 MSK 2021")),
    ScheduleEntry(119, format.parse("Jun 16 08:50:00 MSK 2021")),
    ScheduleEntry(120, format.parse("Jun 16 18:40:00 MSK 2021")),
    ScheduleEntry(121, format.parse("Jun 16 12:50:00 MSK 2021")),
    ScheduleEntry(122, format.parse("Jun 16 15:40:00 MSK 2021")),
    ScheduleEntry(123, format.parse("Jun 16 17:50:00 MSK 2021")),
    ScheduleEntry(124, format.parse("Jun 16 23:10:00 MSK 2021")),
    ScheduleEntry(125, format.parse("Jun 16 19:25:00 MSK 2021")),
    ScheduleEntry(126, format.parse("Jun 16 19:55:00 MSK 2021")),
    ScheduleEntry(127, format.parse("Jun 16 17:10:00 MSK 2021")),
    ScheduleEntry(128, format.parse("Jun 16 17:25:00 MSK 2021")),
    ScheduleEntry(129, format.parse("Jun 16 15:25:00 MSK 2021")),
    ScheduleEntry(130, format.parse("Jun 15 20:40:00 MSK 2021")),
    ScheduleEntry(131, format.parse("Jun 23 12:55:00 MSK 2021")),
    ScheduleEntry(132, format.parse("Jun 16 07:30:00 MSK 2021")),
    ScheduleEntry(133, format.parse("Jun 16 17:35:00 MSK 2021")),
    ScheduleEntry(134, format.parse("Jun 16 17:40:00 MSK 2021")),
    ScheduleEntry(135, format.parse("Jun 16 17:40:00 MSK 2021")),
    ScheduleEntry(136, format.parse("Jun 16 06:00:00 MSK 2021")),
    ScheduleEntry(137, format.parse("Jun 16 19:25:00 MSK 2021")),
    ScheduleEntry(138, format.parse("Jun 16 06:15:00 MSK 2021")),
    ScheduleEntry(139, format.parse("Jun 16 12:45:00 MSK 2021")),
    ScheduleEntry(140, format.parse("Jun 16 17:35:00 MSK 2021")),
    ScheduleEntry(141, format.parse("Jun 16 17:10:00 MSK 2021")),
    ScheduleEntry(142, format.parse("Jun 16 12:15:00 MSK 2021")),
    ScheduleEntry(143, format.parse("Jun 16 17:20:00 MSK 2021")),
    ScheduleEntry(144, format.parse("Jun 16 07:00:00 MSK 2021")),
    ScheduleEntry(145, format.parse("Jun 16 15:30:00 MSK 2021")),
    ScheduleEntry(146, format.parse("Jun 16 19:55:00 MSK 2021")),
    ScheduleEntry(147, format.parse("Jun 17 00:15:00 MSK 2021")),
    ScheduleEntry(148, format.parse("Jun 16 17:15:00 MSK 2021")),
    ScheduleEntry(149, format.parse("Jun 23 19:50:00 MSK 2021")),
    ScheduleEntry(151, format.parse("Jun 23 12:10:00 MSK 2021")),
    ScheduleEntry(152, format.parse("Jun 23 07:45:00 MSK 2021")),
    ScheduleEntry(153, format.parse("Jun 30 23:25:00 MSK 2021")),
    ScheduleEntry(154, format.parse("Jun 23 05:15:00 MSK 2021")),
    ScheduleEntry(155, format.parse("Jun 16 19:50:00 MSK 2021")),
    ScheduleEntry(156, format.parse("Jun 16 19:50:00 MSK 2021")),
    ScheduleEntry(157, format.parse("Jun 16 23:05:00 MSK 2021")),
    ScheduleEntry(158, format.parse("Jun 17 02:20:00 MSK 2021")),
    ScheduleEntry(159, format.parse("Jun 16 17:40:00 MSK 2021")),
    ScheduleEntry(160, format.parse("Jun 16 19:50:00 MSK 2021")),
    ScheduleEntry(161, format.parse("Jun 30 07:30:00 MSK 2021")),
    ScheduleEntry(162, format.parse("Jun 16 19:50:00 MSK 2021")),
    ScheduleEntry(163, format.parse("Jun 30 18:15:00 MSK 2021")),
    ScheduleEntry(164, format.parse("Jun 17 00:15:00 MSK 2021")),
    ScheduleEntry(165, format.parse("Jun 30 23:20:00 MSK 2021")),
    ScheduleEntry(1000, format.parse("Jul 13 17:00:00 MSK 2021")),
    ScheduleEntry(1001, format.parse("Jul 13 17:00:00 MSK 2021")),
    ScheduleEntry(1002, format.parse("Apr 05 18:00:00 MSK 2021"))
  )

  def isSummerTimeNow(): Boolean = {
    val now = Calendar.getInstance()


    val lastSundayOfMarch = Calendar.getInstance()
    lastSundayOfMarch.set(Calendar.MONTH, Calendar.APRIL)
    lastSundayOfMarch.add(Calendar.DAY_OF_MONTH, -1);
    while(lastSundayOfMarch.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      lastSundayOfMarch.add(Calendar.DAY_OF_MONTH, -1)
    }

    val lastSundayOfOctober = Calendar.getInstance()
    lastSundayOfOctober.set(Calendar.MONTH, Calendar.OCTOBER)
    lastSundayOfOctober.add(Calendar.DAY_OF_MONTH, -1);
    while(lastSundayOfOctober.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      lastSundayOfOctober.add(Calendar.DAY_OF_MONTH, -1)
    }

    now.after(lastSundayOfMarch) && now.before(lastSundayOfOctober)
  }
  def normalizeCupScheduleToDayOfWeek(cupSchedules: Seq[ScheduleEntry], dayOfWeek: Int): Seq[ScheduleEntry] = {
    val c = Calendar.getInstance

    c.set(Calendar.DAY_OF_WEEK, dayOfWeek)
    val monday = c.getTime

    cupSchedules
      .map(cupSchedule => {
        if (cupSchedule.date.before(monday)) {
          var newDate = cupSchedule.date
          while (newDate.before(monday)) {
            newDate = new Date(newDate.getTime + 1000L * 60 * 60 * 24 * 7)
          }
          ScheduleEntry(cupSchedule.leagueId, newDate)
        } else {
          var newDate = cupSchedule.date
          while (newDate.after(monday)) {
            newDate = new Date(newDate.getTime - 1000L * 60 * 60 * 24 * 7)
          }
          ScheduleEntry(cupSchedule.leagueId, new Date(newDate.getTime + 1000L * 60 * 60 * 24 * 7))
        }
      })
  }
}
