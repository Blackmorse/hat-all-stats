package com.blackmorse.hattid.web.databases.requests.overview.charts

import sqlbuilder.functions.avg
import sqlbuilder.SqlBuilder.implicits._

object AverageGoalsChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.match_details"
  override protected val aggregateFunction: String = (avg("goals") * 100).toInt32.toString
}
