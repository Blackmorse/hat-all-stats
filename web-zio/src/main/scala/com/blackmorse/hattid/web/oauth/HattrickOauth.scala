package com.blackmorse.hattid.web.oauth

import com.github.scribejava.core.builder.api.DefaultApi10a

object HattrickOauth extends DefaultApi10a {

  override def getRequestTokenEndpoint: String = "https://chpp.hattrick.org/oauth/access_token.ashx"

  override def getAccessTokenEndpoint: String = "https://chpp.hattrick.org/oauth/access_token.ashx"

  override def getAuthorizationBaseUrl: String = "https://chpp.hattrick.org/oauth/authorize.aspx"
}
