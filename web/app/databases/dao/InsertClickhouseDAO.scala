package databases.dao

import anorm.{BatchSql, NamedParameter}
import io.github.gaelrenoux.tranzactio.anorm.tzio
import org.apache.pekko.actor.ActorSystem
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext
import zio.{ZIO, ZLayer}

import java.sql.Connection
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class InsertClickhouseDAO @Inject()(dbApi: DBApi)(implicit ec: DatabaseExecutionContext) {
  private val db = dbApi.database("default")

  def requestLogZio(request: String, params: Seq[(String, String)]): ZIO[Any, Throwable, Array[Int]] = {
    val resource = ZIO.acquireRelease(ZIO.attempt(db.getConnection()))
      (conn => ZIO.succeed(conn.close()))

    ZIO.scoped {
      resource.flatMap { connection =>
        tzio { implicit conn =>
          val keys = params.map(_._1).map(v => s"'$v'").mkString("[", ",", "]")
          val values = params.map(_._2).map(v => s"'$v'").mkString("[", ",", "]")

          val entry = s"insert into hattrick.request_log_buffer values " +
            s"(now(), '$request', $keys, $values)"

          val sql = BatchSql("insert into hattrick.request_log_buffer values ({time}, {request}, {keys}, {values})",
            Seq[NamedParameter]("time" -> java.time.LocalDateTime.now(),
              "request" -> request,
              "keys" -> keys,
              "values" -> values))

          sql.execute()
        }.provide(ZLayer.succeed(connection))
      }
    }
  }

  def requestLog(request: String, params: Seq[(String, String)]) = Future {
    db.withConnection { implicit connection =>
      try {
      val keys = params.map(_._1).map(v => s"'$v'").mkString("[",",","]")
      val values = params.map(_._2).map(v => s"'$v'").mkString("[",",","]")

      val entry = s"insert into hattrick.request_log_buffer values " +
        s"(now(), '$request', $keys, $values)"
        val sql = BatchSql("insert into hattrick.request_log_buffer values ({time}, {request}, {keys}, {values})",
          Seq[NamedParameter]("time" -> java.time.LocalDateTime.now(),
            "request" -> request,
            "keys" -> keys,
            "values" -> values))

        sql.execute()
      } catch {
        case e: Throwable => e.printStackTrace()
      }
    }
  }
}

class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")
