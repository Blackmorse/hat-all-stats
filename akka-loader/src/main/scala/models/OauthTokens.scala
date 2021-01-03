package models

case class OauthTokens(oauthToken: String,
                       oauthCustomerKey: String,
                       clientSecret: String,
                       tokenSecret: String)
