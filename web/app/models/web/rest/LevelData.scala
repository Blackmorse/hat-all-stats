package models.web.rest

import models.web.rest.LevelData.Rounds

trait LevelData {
  def seasonRoundInfo: Seq[(Int, Rounds)]
}

object LevelData {
  type Rounds = Seq[Int]
}