CREATE TABLE hattrick.promotions
(
    `season` UInt8,
    `league_id` UInt16,
    `up_division_level` UInt8,
    `promotion_type` Enum8('auto' = 0, 'qualify' = 1),
    `going_down_teams.team_id` Array(UInt64),
    `going_down_teams.team_name` Array(String),
    `going_down_teams.division_level` Array(UInt8),
    `going_down_teams.league_unit_id` Array(UInt32),
    `going_down_teams.league_unit_name` Array(String),
    `going_down_teams.position` Array(UInt8),
    `going_down_teams.points` Array(UInt8),
    `going_down_teams.diff` Array(Int16),
    `going_down_teams.scored` Array(Int16),
    `going_up_teams.team_id` Array(UInt64),
    `going_up_teams.team_name` Array(String),
    `going_up_teams.division_level` Array(UInt8),
    `going_up_teams.league_unit_id` Array(UInt32),
    `going_up_teams.league_unit_name` Array(String),
    `going_up_teams.position` Array(UInt8),
    `going_up_teams.points` Array(UInt8),
    `going_up_teams.diff` Array(Int16),
    `going_up_teams.scored` Array(Int16)
)
ENGINE = MergeTree()
ORDER BY (season, league_id, up_division_level, promotion_type)
SETTINGS index_granularity = 8192
