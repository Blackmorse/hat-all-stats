package com.blackmorse.hattid.web.models.clickhouse

import anorm.SqlParser.get
import anorm.{RowParser, ~}

case class OauthTokenCh(requestToken: String,
                        accessToken: String,
                        accessTokenSecret: String,
                        creationTime: java.time.LocalDateTime,
                        userId: Long)

object OauthTokenCh {
  val mapper: RowParser[OauthTokenCh] = {
    get[String]("request_token") ~
      get[String]("accessToken") ~
      get[String]("accessTokenSecret") ~
      get[java.time.LocalDateTime]("creation_time") ~
      get[Long]("user_id") map {
      
      case requestToken ~ accessToken ~ accessTokenSecret ~ creationTime ~ userId =>
        OauthTokenCh(requestToken = requestToken,
          accessToken = accessToken,
          accessTokenSecret = accessTokenSecret,
          creationTime = creationTime,
          userId = userId)
    }
  }
}
