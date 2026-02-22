package com.blackmorse.hattid.web.databases.requests.overview.charts

object TeamsNumberOverviewChartRequest extends NumbersOverviewChartRequest {
  override protected val table: String = "hattrick.match_details"
  override protected val aggregateFunction: String = "count()"
}
