package com.blackmorse.hattid.web.databases.dao

import anorm.{BatchSql, NamedParameter}
import io.github.gaelrenoux.tranzactio.DbException
import io.github.gaelrenoux.tranzactio.anorm.tzio
import zio.{ZIO, ZLayer, ZPool}

import java.sql.Connection

object InsertClickhouseDAO  {
  def insertOauthTokens(requestToken: String, accessToken: String, accessTokenSecret: String): ZIO[ZPool[Nothing, Connection], DbException, Unit] = {
    ZIO.scoped {
      for {
        pool       <- ZIO.service[ZPool[Nothing, Connection]]
        connection <- pool.get
        _ <- tzio { implicit conn =>
          BatchSql("insert into hattrick.oauth_tokens values ({requestToken}, {accessToken}, {accessTokenSecret}, now())",
            Seq[NamedParameter]("requestToken" -> requestToken,
              "accessToken" -> accessToken,
              "accessTokenSecret" -> accessTokenSecret)).execute()
        }.provide(ZLayer.succeed(connection))
      } yield ()
    }
  }

  def requestLogZio(request: String, params: Seq[(String, String)]): ZIO[ZPool[Nothing, Connection], DbException, Unit] = {
    ZIO.scoped {
      for {
        pool       <- ZIO.service[ZPool[Nothing, Connection]]
        connection <- pool.get
        _ <- tzio { implicit conn =>
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
      } yield ()
    }
  }
}
