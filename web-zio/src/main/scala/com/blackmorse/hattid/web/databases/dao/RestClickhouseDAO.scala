package com.blackmorse.hattid.web.databases.dao

import anorm.*
import com.blackmorse.hattid.web.databases.ClickhousePool.ClickhousePool
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*
import zio.*


object RestClickhouseDAO {

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

