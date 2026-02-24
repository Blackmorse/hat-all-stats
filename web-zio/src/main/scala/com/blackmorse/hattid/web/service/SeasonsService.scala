package com.blackmorse.hattid.web.service

import java.text.SimpleDateFormat
import java.util.Date

case class DatesRange(min: Date, max: Date)

case class TeamCreatedRanges(season: Int,
                              round: Int,
                              seasonRange: DatesRange,
                              roundRange: DatesRange)
