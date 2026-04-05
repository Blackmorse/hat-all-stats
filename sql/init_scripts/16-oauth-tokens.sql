CREATE TABLE oauth_tokens
(
    `request_token` String,
    `accessToken` String,
    `accessTokenSecret` String,
    `creation_time` DateTime
)
    ENGINE = MergeTree
ORDER BY request_token
