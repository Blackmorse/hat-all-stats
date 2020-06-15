CREATE TABLE match_details
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
    `match_id` UInt64,
    `goals` UInt8,
    `enemy_goals` UInt8,
    `formation` LowCardinality(String),
    `tactic_type` UInt8,
    `tactic_skill` UInt8,
    `rating_midfield` UInt8,
    `rating_right_def` UInt8,
    `rating_left_def` UInt8,
    `rating_mid_def` UInt8,
    `rating_right_att` UInt8,
    `rating_mid_att` UInt8,
    `rating_left_att` UInt8,
    `rating_indirect_set_pieces_def` UInt8,
    `rating_indirect_set_pieces_att` UInt8
)
ENGINE = MergeTree()
PARTITION BY season
ORDER BY (season, league_id, division_level, league_unit_id, round)
SETTINGS index_granularity = 512

