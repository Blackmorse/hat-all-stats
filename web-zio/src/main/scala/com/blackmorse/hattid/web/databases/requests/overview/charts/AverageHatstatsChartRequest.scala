package com.blackmorse.hattid.web.databases.requests.overview.charts

import com.blackmorse.hattid.web.databases.sql.Fields.hatstats
import sqlbuilder.functions.avg


object AverageHatstatsChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.match_details"
  override protected val aggregateFunction: String = avg(hatstats).toInt32.toString
}
