package com.blackmorse.hattid.web.models.web

case class RestStatisticsParameters(page: Int,
                                    pageSize: Int,
                                    sortBy: String,
                                    sortingDirection: SortingDirection,
                                    statsType: StatsType,
                                    season: Int)
