package databases.dao

import anorm.*
import databases.ClickhousePool.ClickhousePool
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*
import zio.*

import javax.inject.{Inject, Singleton}

@Singleton
class RestClickhouseDAO @Inject()() {

  def executeZIO[T](simpleRow: SimpleSql[Row], rowParser: RowParser[T]): ZIO[ClickhousePool, Throwable, List[T]] = {
    ZIO.scoped {
      for {
        pool       <- ZIO.service[ZPool[Nothing, java.sql.Connection]]
        connection <- pool.get
        result     <- ZIO.scoped {
          tzio { implicit conn =>
            simpleRow.as(rowParser.*)
          }.provide(ZLayer.succeed(connection))
        }
      } yield result
    }
  }

  def executeSingleOptZIO[T](simpleSql: SimpleSql[Row], rowParser: RowParser[T]): ZIO[ClickhousePool, Throwable, Option[T]] = {
    ZIO.scoped {
      for {
        pool <- ZIO.service[ZPool[Nothing, java.sql.Connection]]
        connection <- pool.get
        result <- ZIO.scoped {
          tzio { implicit conn =>
            simpleSql.as(rowParser.singleOpt)
          }.provide(ZLayer.succeed(connection))
        }
      } yield result
    }
  }
}

