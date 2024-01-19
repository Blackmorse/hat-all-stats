set allow_experimental_annoy_index = 1;

CREATE TABLE hattrick.match_details_annoy
(
    `match_id` UInt64,
    `goals` UInt8,
    `enemy_goals` UInt8,
    `tactic_type` UInt8,
    `tactic_skill` UInt8,
    `opposite_tactic_type` UInt8,
    `opposite_tactic_skill` UInt8,
    `rating_indirect_set_pieces_def` UInt8,
    `rating_indirect_set_pieces_att` UInt8,
    `opposite_rating_indirect_set_pieces_def` UInt8,
    `opposite_rating_indirect_set_pieces_att` UInt8,
    `vector` Array(Float32),
    INDEX md_annoy_index vector TYPE annoy('L2Distance', 10) GRANULARITY 5
)
ENGINE = MergeTree
ORDER BY tuple()
SETTINGS index_granularity = 8192;
