package databases.dao

import anorm.SQL
import play.api.db.DBApi

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class InsertClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  def requestLog(request: String, params: Seq[(String, String)]) = Future {
    db.withConnection { implicit connection =>
      val keys = params.map(_._1).map(v => s"'$v'").mkString("[",",","]")
      val values = params.map(_._2).map(v => s"'$v'").mkString("[",",","]")

      val entry = s"insert into hattrick.request_log_buffer values " +
        s"(now(), '$request', $keys, $values)"
      try {
        SQL(entry)
          .execute()
      } catch {
        case e => e.printStackTrace()
      }
    }
  }
}
