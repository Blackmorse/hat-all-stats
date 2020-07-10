CREATE TABLE promotions
(
    `season` UInt8,
    `league_id` UInt16,
    `going_down_teams` Nested(
    team_id UInt64,
    team_name String,
    division_level UInt8,
    league_unit_id UInt32,
    league_unit_name String,
    position UInt8,
    points UInt8,
    diff Int16,
    scored Int16,
    promotion_type Enum8('auto' = 0, 'qualify' = 1)),
    `going_up_teams` Nested(
    team_id UInt64,
    team_name String,
    division_level UInt8,
    league_unit_id UInt32,
    league_unit_name String,
    position UInt8,
    points UInt8,
    diff Int16,
    scored Int16,
    promotion_type Enum8('auto' = 0, 'qualify' = 1))
)
ENGINE = MergeTree()
ORDER BY (season, league_id)
