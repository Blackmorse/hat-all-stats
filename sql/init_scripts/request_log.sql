CREATE TABLE hattrick.request_log
(
    `event_time` DateTime,
    `url` String,
    `keys` Array(String),
    `values` Array(String)
)
ENGINE = MergeTree()
ORDER BY event_time
TTL event_time + toIntervalMonth(1)
SETTINGS index_granularity = 8192
