package databases.dao

import org.apache.pekko.actor.ActorSystem
import anorm.*
import io.github.gaelrenoux.tranzactio.anorm._
import io.github.gaelrenoux.tranzactio._
import zio.*
import play.api.db.DBApi
import play.api.libs.concurrent.CustomExecutionContext

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

  def executeSingleOpt[T](simpleRow: SimpleSql[Row], rowParser: RowParser[T]): Future[Option[T]] = Future {
    db.withConnection { implicit connection =>
      simpleRow.as(rowParser.singleOpt)
    }
  }
}

class DatabaseExecutionContext @Inject()(system: ActorSystem) extends CustomExecutionContext(system, "database.clickhouse")
