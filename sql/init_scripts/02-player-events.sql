 CREATE TABLE IF NOT EXISTS hattrick.player_events
(
    `season` UInt8, 
    `round` UInt8, 
    `player_id` UInt64, 
    `yellow_cards` UInt8, 
    `red_cards` UInt8, 
    `goals` UInt8, 
    `injury` UInt8, 
    `left_minute` Int8
)
ENGINE = MergeTree()
ORDER BY (season, round, cityHash64(player_id), yellow_cards, red_cards, goals, injury, left_minute)
SAMPLE BY cityHash64(player_id)
SETTINGS index_granularity = 1024;
