CREATE TABLE hattrick.team_rankings
(
    `rank_type` Enum8('league_id' = 0, 'division_level' = 1),
    `season` UInt8,
    `league_id` UInt16,
    `round` UInt8,
    `division_level` UInt8,
    `league_unit_id` UInt32,
    `league_unit_name` LowCardinality(String),
    `team_id` UInt64,
    `team_name` String,
    `match_id` UInt64,
    `hatstats` UInt16,
    `hatstats_position` UInt32,
    `attack` UInt16,
    `attack_position` UInt32,
    `midfield` UInt16,
    `midfield_position` UInt32,
    `defense` UInt16,
    `defense_position` UInt32,
    `tsi` UInt32,
    `tsi_position` UInt32,
    `salary` UInt32,
    `salary_position` UInt32,
    `rating` UInt16,
    `rating_position` UInt32,
    `rating_end_of_match` UInt16,
    `rating_end_of_match_position` UInt32,
    `age` Float64,
    `age_position` UInt32,
    `injury` Int8,
    `injury_position` UInt32,
    `injury_count` Int8,
    `injury_count_position` UInt32,
    `power_rating` UInt16,
    `power_rating_position` UInt32
)
ENGINE = MergeTree()
PARTITION BY season
ORDER BY (season, league_id, team_id, round)
SETTINGS index_granularity = 512
