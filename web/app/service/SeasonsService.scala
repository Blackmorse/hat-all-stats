package service

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.{Inject, Singleton}

case class DatesRange(min: Date, max: Date)

case class TeamCreatedRanges(season: Int,
                              round: Int,
                              seasonRange: DatesRange,
                              roundRange: DatesRange)
