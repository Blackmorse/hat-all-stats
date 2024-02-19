CREATE TABLE IF NOT EXISTS hattrick.upload_history (
    `league_id` UInt16,
    `season` UInt8,
    `round` UInt8,
    `is_league_match` UInt8,
    `time` DateTime DEFAULT now()
)
ENGINE = MergeTree()
ORDER BY (season, round, league_id)