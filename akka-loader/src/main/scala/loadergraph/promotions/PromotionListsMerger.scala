package loadergraph.promotions

import models.clickhouse.PromotionModelCH
import models.stream.StreamTeam

import java.util
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.{IterableHasAsScala, IteratorHasAsScala, MutableSeqHasAsJava}

object PromotionListsMerger {
  def merge(leagueId: Int, season: Int, upDivisionLevel: Int, promoteType: PromoteType,
             leftList: List[StreamTeam], rightList: List[StreamTeam]): List[PromotionModelCH] = {
    val result = ArrayBuffer[PromotionModelCH]()

    var i = 0
    while (i < leftList.size) {
      val leftMinInd = i

      var leftMaxInd = i
      while (leftMaxInd < leftList.size && (leftList(leftMinInd) samePosition leftList(leftMaxInd))) leftMaxInd += 1
      leftMaxInd -= 1

      var rightMinInd = i
      while (rightMinInd >= 0 && (rightList(rightMinInd) samePosition rightList(i))) rightMinInd -= 1
      rightMinInd += 1

      var rightMaxInd = leftMaxInd
      while (rightMaxInd < rightList.size && (rightList(rightMaxInd) samePosition rightList(leftMaxInd))) rightMaxInd += 1
      rightMaxInd -= 1

      val leftArray = (leftMinInd to leftMaxInd).map(leftList(_))

      val rightArray = (rightMinInd to rightMaxInd).map(rightList(_))


      val promotionModel = PromotionModelCH(leagueId = leagueId,
        season = season,
        upDivisionLevel = upDivisionLevel,
        promoteType = promoteType,
        downTeams = leftArray.toBuffer,
        upTeams = rightArray.toList
      )

      result += promotionModel

      i = leftMaxInd + 1
    }

    val javaResult = new util.ArrayList(result.asJava)
    val iterator = javaResult.iterator

    var previous = iterator.next
    var next: PromotionModelCH = null

    do {
      next = iterator.next
      if (previous.upTeams == next.upTeams) {
        iterator.remove()
        previous.addDownTeams(next.downTeams)
      }
      else previous = next
    } while (iterator.hasNext)

    javaResult.asScala.toList
  }
}
