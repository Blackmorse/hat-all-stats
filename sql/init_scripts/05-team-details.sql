CREATE TABLE hattrick.team_details
(
	    `season` UInt8,
	    `league_id` UInt16,
	    `division_level` UInt8,
	    `league_unit_id` UInt32,
	    `league_unit_name` LowCardinality(String),
	    `team_id` UInt64,
	    `team_name` String,
            `founded_date` Date,
	    `round` UInt8,
	    `power_rating` UInt16,
	    `home_flags` UInt8,
	    `away_flags` UInt8,
	    `fanclub_size` UInt16,
	    `trophies_number` UInt16,
	    `number_of_victories` UInt8,
	    `number_of_undefeated` UInt8
)
ENGINE = MergeTree()
PARTITION BY season
ORDER BY (season, league_id, division_level, league_unit_id, round)
SETTINGS index_granularity = 512
