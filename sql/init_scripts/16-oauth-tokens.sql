CREATE TABLE oauth_tokens
(
    `request_token` String,
    `accessToken` String,
    `accessTokenSecret` String,
    `creation_time` DateTime,
    `user_id` UInt64
)
    ENGINE = MergeTree
ORDER BY request_token
