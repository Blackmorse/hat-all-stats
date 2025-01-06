package databases.requests.model.promotions

import hattid.CommonData
import play.api.libs.json.{Json, OWrites}
import utils.Romans



case class PromotionWithType(upDivisionLevel: Int,
                             upDivisionLevelName: String,
                             downDivisionLevelName: String,
                             promoteType: String,
                             promotions: List[Promotion])


object PromotionWithType {
  implicit val writes: OWrites[PromotionWithType] = Json.writes[PromotionWithType]

  def convert(promotions: List[Promotion]): Seq[PromotionWithType] = {
    promotions.groupBy(promotion => (promotion.upDivisionLevel, promotion.promoteType))
      .toSeq.sortBy(_._1)
      .map{case((upDivisionLevel, promoteType), promotions) =>
        PromotionWithType(upDivisionLevel,
          if(upDivisionLevel == 1) CommonData.higherLeagueMap(promotions.head.leagueId).leagueUnitName else Romans(upDivisionLevel),
          Romans(upDivisionLevel + 1),
          promoteType,
          promotions)
      }
  }
}