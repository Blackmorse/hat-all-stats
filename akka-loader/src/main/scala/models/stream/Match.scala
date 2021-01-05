package models.stream

import java.util.Date

case class Match(id: Long,
                 round: Int,
                 date: Date,
                 season: Int,
                 team: Team)
