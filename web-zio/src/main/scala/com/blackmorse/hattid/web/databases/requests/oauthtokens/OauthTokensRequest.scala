package com.blackmorse.hattid.web.databases.requests.oauthtokens

import anorm.RowParser
import com.blackmorse.hattid.web.databases.dao.RestClickhouseDAO
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest
import com.blackmorse.hattid.web.databases.requests.ClickhouseRequest.DBIO
import com.blackmorse.hattid.web.models.clickhouse.OauthTokenCh
import sqlbuilder.Select

object OauthTokensRequest extends ClickhouseRequest[OauthTokenCh] {
  override val rowParser: RowParser[OauthTokenCh] = OauthTokenCh.mapper
  
  def execute(requestToken: String): DBIO[Option[OauthTokenCh]] = wrapErrorsOpt {
    import sqlbuilder.SqlBuilder.implicits._

    val builder = Select(
      "request_token",
      "accessToken",
      "accessTokenSecret",
      "creation_time",
      "user_id"
    ).from("hattrick.oauth_tokens")
      .where
      .and(s"request_token = '$requestToken'")
      .orderBy("creation_time".desc)
      .limit(1)
    
    RestClickhouseDAO.executeSingleOptZIO(builder.sqlWithParameters().build, rowParser)
  }
}
