package databases.dao

import org.apache.pekko.actor.ActorSystem
import anorm.*
import io.github.gaelrenoux.tranzactio.anorm._
import io.github.gaelrenoux.tranzactio._
import zio.*
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext

import javax.inject.{Inject, Singleton}

@Singleton
class RestClickhouseDAO @Inject()(dbApi: DBApi) {
  private val db = dbApi.database("default")

  def executeZIO[T](simpleRow: SimpleSql[Row], rowParser: RowParser[T]): ZIO[Any, Throwable, List[T]] = {
    val resource = ZIO.acquireRelease(ZIO.attempt(db.getConnection()))
        (conn => ZIO.succeed(conn.close()))

    ZIO.scoped {
      resource.flatMap { connection =>
        tzio { implicit conn =>
          simpleRow.as(rowParser.*)
        }.provide(ZLayer.succeed(connection))
      }
    }
  }

  def executeSingleOptZIO[T](simpleSql: SimpleSql[Row], rowParser: RowParser[T]): ZIO[Any, Throwable, Option[T]] = {
    val resource = ZIO.acquireRelease(ZIO.attempt(db.getConnection()))
      (conn => ZIO.succeed(conn.close()))

    ZIO.scoped {
      resource.flatMap { connection =>
        tzio { implicit conn =>
          simpleSql.as(rowParser.singleOpt)
        }.provide(ZLayer.succeed(connection))
      }
    }
  }
}

class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")
