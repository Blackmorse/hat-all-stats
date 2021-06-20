CREATE TABLE hattrick.player_stats
(
    `season` UInt8,
    `league_id` UInt16,
    `division_level` UInt8,
    `league_unit_id` UInt32,
    `league_unit_name` String,
    `team_id` UInt64,
    `team_name` String,
    `time` DateTime,
    `dt` Date DEFAULT toDate(time),
    `round` UInt8,
    `cup_level` UInt8,
    `cup_level_index` UInt8,
    `match_id` UInt64,
    `player_id` UInt64,
    `first_name` String,
    `last_name` String,
    `age` UInt8,
    `days` UInt8,
    `role_id` UInt8,
    `played_minutes` UInt8,
    `rating` UInt8,
    `rating_end_of_match` UInt8,
    `injury_level` Int8,
    `tsi` UInt32,
    `salary` UInt32,
    `yellow_cards` UInt8,
    `red_cards` UInt8,
    `goals` UInt8,
    `nationality` UInt8
)
ENGINE = MergeTree()
PARTITION BY season
ORDER BY (season, league_id, division_level, league_unit_id, round)
SETTINGS index_granularity = 4096
