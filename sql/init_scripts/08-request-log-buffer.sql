 CREATE TABLE IF NOT EXISTS hattrick.request_log_buffer
(
    `event_time` DateTime,
    `url` String,
    `keys` Array(String),
    `values` Array(String)
)
ENGINE = Buffer('hattrick', 'request_log', 16, 60, 120, 1000, 10000, 100000, 100000)
