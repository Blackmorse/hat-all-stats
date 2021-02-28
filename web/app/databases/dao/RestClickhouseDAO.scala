package databases.dao

import anorm.{Row, RowParser, SimpleSql}
import play.api.db.DBApi

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class RestClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  def execute[T](simpleRow: SimpleSql[Row],
                 rowParser: RowParser[T]): Future[List[T]] = Future {
    db.withConnection{ implicit connection =>
      simpleRow.as(rowParser.*)
    }
  }

  def executeSingleOpt[T](simpleRow: SimpleSql[Row], rowParser: RowParser[T]): Future[Option[T]] = Future {
    db.withConnection { implicit connection =>
      simpleRow.as(rowParser.singleOpt)
    }
  }
}
